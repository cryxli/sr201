# Simple PHP "Cloud Service" Application for SR-201 Ethernet Relay Board
## Introduction
The device supports Cloud Service connectivity. Using this, users can remote control the relays without direct connectivity to the device itself. This is achieved by having the device calling out the Cloud Service to get the instructions and pass the relay statuses.

## Technical Background
Basically, the device regularly sends POST requests to a Web Service, to the configured web server (default *connect.tutuuu.com*). The device only posts a single string having the *Content-Type: application/json*. 

```
POST /SyncServiceImpl.svc/ReportStatus HTTP/1.1
User-Agent: SR-201W/M96Y
Content-Type: application/json
Host: sr201.000webhostapp.com
Content-Length: 30

"F0123456789ABCXXXXXX00000000"
```
The first 13 characters after 'F' is the device serial (all hex), followed by the 6-digit password (numerical, plain text) and then the 8-digit current relay state.

There is then a 60 second TTL for the response, which should be of the form:

```
HTTP/1.1 200 OK
Date: Tue, 25 Jul 2017 16:56:15 GMT
Content-Type: application/json; charset=utf-8
Content-Length: 3
Connection: keep-alive
Server: awex
X-Xss-Protection: 1; mode=block
X-Content-Type-Options: nosniff
X-Request-ID: 4e7cae65837705fad795cccdc0dbec9f

"A"
```
This is the constant-state response - simply indicating that both endpoints are live and ready.
(As with standard HTTP, all lines must terminate with the windows style newline "\r\n")

To change the relay states, the final line must change from "A" to any of the standard port commands, until the change is acknowledged in the subsequent "pings".
Eg. "A11" to switch relay 1 on, etc.

The device cannot manage chunked http response, so the *Content-length:* header parameter must be set.

According my experience if *"A"* is received, the device will send the next request in 1 second. In case there was an error it goes up to 10 seconds.

## Sample Implementation
The sample php implementation is providing a simple php page to process the POST request from the device. After some checking all it does is that it stores the current status of the relays into a file named *MD5(DeviceSerial+Password)_sta*. At the same time it reads the *MD5(DeviceSerial+Password)_cmd* file and passes back the content of the file. The file content is rewritten to the default *"A"* so the next request will just get the 'ping' response.

There is a simple user interface created where the users can enter the Device Serial and the Password to identify the device (Actually the username and password entered are used to define the filename that is used for the communication between the "GUI" and the "Cloud Service".) The Serial and the passwords are stored locally in cookies.

Then the channel can be selected (Channel 1 is the default) with the Action (Pull or Release) and optionally the timeout can be defined (Jog is the default as I'm using this service to open my gate :) ).

The pages constructs the command text and saves it to the *MD5(DeviceSerial+Password)_cmd* file. You can just query the status without sending any instruction to the device.


## Test Cloud Service
The sample code is hosted on a free hosting site and accessible at the following URL:
<http://sr201.000webhostapp.com>

To configure your device to use this sample service, all you need to do is to send the following strings to the config TCP port (5111) to set the server:
  ```
  #91111,sr201.000webhostapp.com;
  ```
  and this one to save the changes:
  ```
  #71111;
  ```
  

  See <https://github.com/cryxli/sr201/wiki/Config-commands> for more details.

## Disclaimer

The hosted service is for demonstration purposes only, provided as it is without any support whatsoever. Use the service on your own risk.
The service logs the IP address of both the device and the client requests, but does not store the password, only the hash. Please check the source code for more details.

Thanks and credit goes to **anakhaema** for the initial post.
