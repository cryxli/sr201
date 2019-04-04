#!/usr/bin/python3
#
# SYNOPSIS
#   sr-201-relay.py relay-hostname [command...]
#
#
# DESCRIPTION
#
#   Configures and controls a SR-201 Ethernet relay.  If no commands are given
#   'config' is assumed.  If you don't supply relay-hostname a short help
#   message will be issued listing the available commands.
#
# BUGS
#
#   Although this program can be used to issue control commands to the relay
#   I recommend doing it directly instead.  It will be be faster, more
#   reliable, and the task is so simple the code will be shorter.  The
#   primary purpose of this code is to document how to do it, with a working
#   example.
#
#
# THE DEVICE: Factory Defaults
#
#    Default IP Address:        192.168.1.100
#    Port 6722:                 TCP control
#    Port 6723:                 UDP control
#    Port 5111:                 TCP Configuration
#
#    The device can be reset to these defaults by shorting the CLR pins
#    on the header next to the RJ45 connector.  CLR is adjacent to the +5V
#    and P30 pins.
#
# THE DEVICE: Commands that can be sent over the TCP and UDP control ports
#
#    Commands are ASCII strings that must be sent in one packet
#    (even for TCP):
#
#        0R     No operation (but return status).
#
#        1R*    Close relay if it's open, wait approx 1/2 a second, open
#               relay.
#
#        1R     Close relay if it's open.
#
#        1R:0   Close relay if it's open.
#
#        1R:n   Close relay if it's open, then in n seconds (1 <= n <= 65535)
#               open it.
#
#        2R     Open relay if it's closed.
#
#    Where:
#
#        R      is the relay number, '1' .. '8'.  The main board has relay's
#               '1' and '2', the extension board (if present) has the rest.
#               If R is 'X' all relays are effected.
#
#    If the command is sent over TCP (not UDP, TCP only), the relay will
#    reply with a string of 8 0's and 1's, representing the "before" command
#    was executed" state of relay's 1..8 in that order.  A '0' is sent if the
#    relay is open, '1' if closed.
#
#
# THE DEVICE: TCP Configuration
#
#    Commands are ASCII strings that must be sent in one TCP packet.  'i'
#    is a random number in the range '1000' .. '9999':
#
#    #1i;       Query State.  Response is a comma separated list of fields
#               terminated by a semicolon (';').  Example response:
#
#                 >192.168.1.100,255.255.255.0,192.168.1.1,,0,435,F44900F6087457000000,192.168.1.1,connect.tutuuu.com,0;
#
#               Fields in order of appearance are:
#
#               ID  Value in example     Description
#               --  -------------------- ---------------------------------
#                2  192.168.1.100        Devices IP Address.
#                3  255.255.255.0        Devices subnet mask.
#                4  192.168.1.1          Gateway.
#                5                       Unknown.
#                6  0                    '1'=Save relay state across poweroff.
#                7  435                  Software version is 1.0.435 / reset.
#               na  F44900F6087457000000 Device serial number.
#                8  192.168.1.1          DNS Server to look up cloud service.
#                9  connect.tutuuu.com   Cloud service.
#                A  0                    Cloud service enabled if = '1'.
#
#
#    #Di,F;    Set the state whose ID is 'D' to value 'F'.  For example:
#
#                #61234,1;      Persist relay state across power cycle.
#                #71234;        Reset the device so changes take effect.
#
#              ID 'B' sets the cloud service password.
#
#
# THE DEVICE: Cloud operation
#
#   If cloud operation is enabled (by setting ID "A" to "1"), the device sends
#   a HTTP "POST" request every second or so to server stipulated in setting
#   ID "9".  The request is:
#
#      POST /SyncServiceImpl.svc/ReportStatus HTTP/1.1
#      User-Agent: SR-201W/M96Y
#      Content-Type: application/json
#      Host: 192.168.1.1
#      Content-Length: 30
#
#      "F0123456789ABCXXXXXX00000000"
#
#   The post body contains the following fields:
#
#       F0123456789ABCXXXXXX00000000
#       \------------/\----/\------/
#             |         |      |
#             |         |      +---- State of relays 1..8, 0=open, 1=closed.
#             |         +----------- Password.
#             +--------------------- Serial number.
#
#   The response must have the Content-Length header set, and the body must
#   be a singe application/json string starting with "A" optionally followed
#   by a single relay control command, eg:
#
#      HTTP/1.1 200 OK
#      Content-Type: application/json
#      Content-Length: 7
#
#      "A11:2"
#
#
# Home page: https://sr-201-relay.sourceforge.net/
#
# Author: Russell Stuart, russell-debian@stuart.id.au
#
# License
# -------
#
# Copyright (c) 2017 Russell Stuart.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published
# by the Free Software Foundation, either version 3 of the License, or (at
# your option) any later version.
#
# The copyright holders grant you an additional permission under Section 7
# of the GNU Affero General Public License, version 3, exempting you from
# the requirement in Section 6 of the GNU General Public License, version 3,
# to accompany Corresponding Source with Installation Information for the
# Program or any work based on the Program. You are still required to
# comply with all other Section 6 requirements to provide Corresponding
# Source.
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
import warnings; warnings.simplefilter('default')
import os
import re
import select
import socket
import sys
import time


class Sr201(object):
    _IPV4_RE = '[.]'.join(
        ('(?:[0-9]{1,2}|[01][0-9]{2}|2[0-4][0-9]|25[0-5])',) * 4)
    _hostname = None
    _port = None
    _soc = None
    trace = False

    CONFIG_NAMES = [
        'ip',
        'netmask',
        'gateway',
        '(unknown)',
        'power_persist',
        'version',
        'serial',
        'dns',
        'cloud_server',
        'cloud_enabled',
        'cloud_password']

    PORT_CONFIG = 5111
    PORT_CONTROL = 6722

    def __init__(self, hostname):
        self._hostname = hostname
        self.open()

    def flush(self):
        s = select.select([self._soc.fileno()], [], [], 0.001)
        while s[0]:
            data = self._soc.recv(4096)
            if self.trace:
                sys.stdout.write('~ ' + data.encode('latin1') + '\n')
            s = select.select([self._soc.fileno()], [], [], 0.0)

    def open(self, port=None):
        port = port or self.PORT_CONTROL
        if port == self._port:
            self.flush()
            return
        self.close()
        self._soc = socket.create_connection((self._hostname, port))
        self._port = port

    def close(self):
        if self._soc:
            self.flush()
            self._soc.close()
            self._port = None
            self._soc = None

    def recv(self):
        response = self._soc.recv(4096).decode('latin1')
        if self.trace:
            sys.stdout.write('< ' + response + '\n')
        return response

    def send(self, data):
        if self.trace:
            sys.stdout.write('> %s\n' % (data,))
        return self._soc.send(data.encode('latin1'))

    def send_config(self, command, op, value):
        self.open(self.PORT_CONFIG)
        param = value is not None and ',' + value or ''
        self.send('#' + op + '9999' + param + ';')
        response = self.recv()
        if (
            not response or response[0] != '>' or response[-1] != ';' or
            op != '1' and response != '>OK;'
        ):
            me = os.path.basename(sys.argv[0])
            msg = "%s: invalid response to %r: %r\n" % (me, command, response)
            sys.stderr.write(msg)
            sys.exit(1)
        return response

    def do_close(self, command):
        match = 'close:([1-8Xx])([~]|:[1-9][0-9]{0,4}|6[0-4][0-9]{3})?$'
        match = re.match(match, command)
        if not match:
            usage('Invalid %r' % (command,))
        data = '1' + match.group(1).upper()
        if match.group(2) == '~':
            data += '*'
        elif match.group(2):
            data += match.group(2)
        self.open()
        self.send(data)

    def do_cloud_enabled(self, command):
        match = re.match('(?i)cloud_enabled=(0|1|n|no|y|yes)$', command)
        if not match:
            usage('Invalid %s' % (command,))
        cloud_enabled = match.group(1) in ('1', 'y', 'yes') and '1' or '0'
        self.send_config(command, 'A', cloud_enabled)

    def do_cloud_password(self, command):
        match = re.match('(?i)cloud_password=([0-9]{6})$', command)
        if not match:
            usage('%s must be exactly 6 digits' % (command,))
        self.send_config(command, 'B', match.group(1))

    def do_cloud_server(self, command):
        match = (
            'cloud_server=((?:[-a-z0-9]+[.])+[-a-z0-9]{2,}|' +
            self._IPV4_RE + ')$')
        match = re.match(match, command)
        if not match:
            usage('Invalid %s' % (command,))
        self.send_config(command, '9', match.group(1))

    def do_config(self, command):
        response = self.send_config(command, '1', None)
        # >192.168.1.100,255.255.255.0,192.168.1.1,,0,435,F44900F6087457000000,192.168.1.1,connect.tutuuu.com,0;
        values = response[1:-1].split(',')
        if len(values) != len(self.CONFIG_NAMES) - 1:
            raise Exception('Expected 10 values in response: %r' % (response,))
        for i in range(len(values)):
            sys.stdout.write('%s=%s\n' % (self.CONFIG_NAMES[i], values[i]))
        sys.stdout.write('%s=(not-sent)\n' % (self.CONFIG_NAMES[-1],))

    def do_dns(self, command):
        match = re.match('dns=(' + self._IPV4_RE + ')$', command)
        if not match:
            usage('Invalid %s' % (command,))
        self.send_config(command, '8', match.group(1))

    def do_gateway(self, command):
        match = re.match('gateway=(' + self._IPV4_RE + ')$', command)
        if not match:
            usage('Invalid %s' % (command,))
        self.send_config(command, '4', match.group(1))

    def do_ip(self, command):
        match = re.match('ip=(' + self._IPV4_RE + ')$', command)
        if not match:
            usage('Invalid %s' % (command,))
        self.send_config(command, '2', match.group(1))

    def do_netmask(self, command):
        match = re.match('netmask=(' + self._IPV4_RE + ')$', command)
        if not match:
            usage('Invalid %s' % (command,))
        ip = match.group(1).split('.')
        ip = sum(256 ** (3 - i) * int(ip[i], 10) for i in range(len(ip)))
        if all(ip != 2 ** 32 - 2 ** i for i in range(32)):
            usage('%s is not a CIDR' % (command,))
        self.send_config(command, '3', match.group(1))

    def do_open(self, command):
        match = re.match('open:([1-8Xx])$', command)
        if not match:
            usage('Invalid open %r' % (command,))
        data = '2' + match.group(1).upper()
        self.open()
        self.send(data)

    def do_pause(self, command):
        match = re.match('pause:([0-9]+(?:[.][0-9]*)?|[.][0-9]+)$', command)
        if not match:
            usage('Invalid pause %r' % (command,))
        pause = float(match.group(1))
        time.sleep(pause)
        if pause > 10:
            self.close()

    def do_power_persist(self, command):
        match = re.match('(?i)power_persist=(0|1|n|no|y|yes)$', command)
        if not match:
            usage('Invalid %s' % (command,))
        power_persist = match.group(1) in ('1', 'y', 'yes') and '1' or '0'
        self.send_config(command, '6', power_persist)

    def do_reset(self, command):
        if command != 'reset':
            usage('Invalid reset %r' % (command,))
        self.send_config(command, '7', None)
        self.close()

    def do_status(self, command):
        if command != 'status':
            usage('Invalid status %r' % (command,))
        self.open()
        self.send('00')
        states = self.recv()
        sys.stdout.write('relay status: ' + ' '.join(
            '%d-%s' % (r + 1, states[r] == '0' and 'open' or 'closed')
            for r in range(len(states))) + '\n')


#
# ya flubbed it, dearie.
#
def usage(msg=None):
    def w(s, d=None):
        def split(w):
            i = (i for i in range(1, len(w) + 1) if len(' '.join(w[:i])) > 64)
            i = next(i, 64)
            r = w[:i]
            w[:i] = []
            return ' '.join(r)
        if not d:
            sys.stderr.write(s + '\n')
        else:
            w = d.split()
            sys.stderr.write(s + ' ' * max(16 - len(s), 1) + split(w) + '\n')
            while w:
                sys.stderr.write(' ' * 16 + split(w) + '\n')
    me = os.path.basename(sys.argv[0])
    if msg:
        w('%s: %s' % (me, msg))
    else:
        w('usage: %s [--trace] relay-hostnamme command...' % (me,))
        w('options:')
        w('  --trace', 'Print a trace of all I/O to the relay')
        w('commands:')
        w('  close:R', 'Close relay R.')
        w('  close:R:T', 'Close relay R, then after T seconds open it.')
        w('  close:R~', 'Close relay R, then in about 1/2 a second open it.')
        w('  config', 'Print the devices configuration settings to stdout.')
        w('  CONFIG=VALUE',
            'Set the configuration item "CONFIG" to "VALUE".')
        w('  open:R', 'Open relay R.')
        w('  pause:n', 'Pause for n seconds.')
        w('  reset', 'Reset device, applying changes to the configuration.')
        w('  status', 'Print the states of all relays.')
        w('R:')
        w('  The relay, a digit 1..8, or X for all relays.')
    sys.exit(1)


#
# Entry point.
#
def main(argv=sys.argv):
    i = 1
    trace = False
    if len(argv) > i and argv[i] == '--trace':
        trace = True
        i += 1
    if len(argv) == i:
        usage()
    if argv[i].startswith("-"):
        usage("unknown option %r" % (argv[i],))
    sr_201 = Sr201(argv[i])
    i += 1
    sr_201.trace = trace
    commands = argv[i:] or ('config',)
    for command in commands:
        cmd = re.match('[_a-z]+', command)
        if not cmd or not hasattr(sr_201, 'do_' + cmd.group(0)):
            usage('Unrecognised command %r.' % (command,))
        getattr(sr_201, 'do_' + cmd.group(0))(command)
    sr_201.close()


if __name__ == '__main__':
    main()
