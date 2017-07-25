<?php
error_reporting(E_ERROR | E_PARSE);

//Make sure that it is a POST request and application/json
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST') != 0){
    header('HTTP/1.1 500 Internal Server Error');
    echo "Only POST request are accepted!";
    exit(0);
}

$contentType = isset($_SERVER["CONTENT_TYPE"]) ? trim($_SERVER["CONTENT_TYPE"]) : '';
if(strcasecmp($contentType, 'application/json') != 0){
    header('HTTP/1.1 500 Internal Server Error');
    echo "Invalid content type!  You are not whom I expected...";
    exit(0);
}
 
//Get the RAW post data that is passed as JSON, but actually it is a string only, so we don't decode to JSON
$request = trim(file_get_contents("php://input"));

//Basic check of the request (28 + 2")
if(strlen($request)!=30)
{
    header('HTTP/1.1 500 Internal Server Error');
    echo "Incorrect content! You are not whom I expected...";
    exit(0);
}

//Load config based on device serial and pwd if exist
$device = "../devices/" . md5(substr($request,1,20));
$status = substr($request,21,8);

//Try to read the command file
try {
    $command = file_get_contents($device . "_cmd");
}
catch(Exception $e) {
  //You may want to log this
}

if($command === FALSE)
{
    //If we cannot read, we don't know the device (or other error happened) but anyhow we don't allow the device to process the request
    //First the client app (device.php) must enter the serial/password to generate the cmd file for the device
    header('HTTP/1.1 500 Internal Server Error');
    echo $device . " device is not known yet.";
    exit(0);
}

//Check the previously stored device (relay) status
$oldstatus = file_get_contents($device . "_sta");
if($oldstatus === FALSE || $status != $oldstatus)
{
    //If changed or new we write the states received
    file_put_contents($device . "_sta", $status);
}

//Lets put together the response
header('Content-Type: application/json; charset=utf-8');
//The next line is required because the device cannot manage chunked http response
header("Content-length: " . strlen($command));
echo $command;

//Reset command, write log if something happened
if($command !='"A"' || $oldstatus === FALSE || $status != $oldstatus)
{
    file_put_contents($device .'_cmd', '"A"');

    $logfile='../logs/devices.log';
    $content =date('Y-m-d H:i:s') . ': ' . $_SERVER['REMOTE_ADDR']." Command processed: " . $command . " by device (hash): " . md5(substr($request,1,20)) . " Status is:" . $status . "\n";
    file_put_contents($logfile, $content, FILE_APPEND | LOCK_EX);
}

?>