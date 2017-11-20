# Simple Client Application for SR-201 Ethernet Relay Board

Resently I ordered a little board 50mm x 70mm with two relays. They can be switched by sending commands over TCP or UDP. The only problem with it is, that the code examples and instruction manual are entierly written in Chinese. Therefore, I created this repo to keep track of my findings regarding the SR-201-2.

## Models

The same idea, switching relays over ethernet, resulted in at least four different models of the SR-201:

* SR-201-1CH - Cased, single relay
* SR-201-2 - Plain board, two relays (mine)
* SR-201-RTC - Cased, four relays
* SR-201-E8B - Plain board, eight relays

They all seem to work with the same chip and software. Although, e.g., the SR-201-2 only has two relays, it also has an extension port with another 6 pins which can be switched, too.

## Protocols and Ports

The board supports the protocols ARP, ICMP, IP, TCP, UDP. Or short, everything needed to allow TCP and UDP connections.

When connected over TCP (port **6722**), the board will only accept 6 connections at a time. To prevent starving, it will close TCP connection after they have been idle for 15 seconds.

Since UDP (port **6723**) is not an end-to-end connection, there are no restrictions. But it is noteworthy that the board will execute UDP commands, but it will never answer. Therefore querying the state of the relays has to be done over TCP.

The board also listens to the TCP port **5111**. Over this connection the board can be configured. E.g., its static IP address can be changed.

## Factory Defaults

* Static IP address : 192.168.1.100
* Subnet mask : 255.255.255.0
* Default Gateway : 192.168.1.1
* Persistent relay state when power is lost : off
* Cloud service password : 000000
* DNS Server : 192.168.1.1
* Cloud service : connect.tutuuu.com
* Cloud service enabled: false

## Example Code

This repo contains the following modules:

* sr201-config-client - Client to read and change the config of the board.
* sr201-client - Simple client with 8 toggle buttons to change the state of the relays.
* sr201-server - REST interface to change the state of the relays.
* sr201-php-cloud-service - Example implementation of a cloud service back-end in PHP provided by [hobpet](https://github.com/hobpet) following the findings of [anakhaema](https://github.com/anakhaema).
 
Maven will create an executable JAR in each of the modules target directories.

## Scripts

In addition to my Java code examples that are clearly intended as a replacement for the default VB and Delphi programs, I added a scripts directory that contains simpler more pragmatic approaches to the SR-201 communication scheme.

* perl-config-script - A PERL script to manipulate the board's configuration by Christian DEGUEST.

Many thanks to anyone who contributed to this knowledge base!

## Own Scripts

If you want to quickly setup your SR-201 without even starting a script or anything else, just check the protocol [https://github.com/cryxli/sr201/wiki/Config-commands](Config commands) and e.g. send a command via netcat:

    printf "#11111;" | nc [yourip] 5111

Note: It is crucial to use printf here, as newlines are seen as errors. It drove me crazy to find out about this one.
