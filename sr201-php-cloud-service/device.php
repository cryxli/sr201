<?php
error_reporting(E_ERROR | E_PARSE);

//We store the serial and the password in local cookie, the server does not store the serial nor the password
setcookie('serial', $_REQUEST['serial'] , time() + (86400 * 365), "/"); // 86400 = 1 day
setcookie('password', $_REQUEST['password'] , time() + (86400 * 365), "/"); // 86400 = 1 day

//Command filename is generated from the hashed serial and password
$device = md5($_REQUEST['serial'] . $_REQUEST['password']);
$file = './devices/' . $device .'_cmd';
$current = file_get_contents($file);

if($current != "\"A\"" && $current!=FALSE)
{
    //We only accept new commands if the previous one was processed by the device
    $result = 'Device ' . $device .  ' is not ready to take action!' . $current;
}
elseif($_POST['submit']=="Apply")
{
    $action="\"A" . $_POST['action'] .  $_POST['channel'] . $_POST['timeout']."\"";
    // Write the action to the file
    file_put_contents($file, $action);
    $result = 'Device ' . $device .  ' will receive the following command: ' . $action;
}
else
{
    $result = 'Device ' . $device .  ' status query only.'; 
}

$logfile='./logs/clients.log';
$content=date('Y-m-d H:i:s') . ': ' . $_SERVER['REMOTE_ADDR']. ' ' . $result . "\n";
file_put_contents($logfile, $content, FILE_APPEND | LOCK_EX);
?>

<html>
    <header><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>sr201 Contol</title>
        <link rel="stylesheet" type="text/css" href="form.css">
    </header>

    <body>
        <div class="form-style-5">
        <form action="device.php" method="post">
            <input type="hidden" name="serial" value="<?php echo $_REQUEST['serial']?>">
            <input type="hidden" name="password" value="<?php echo $_REQUEST['password']?>">
            
            <fieldset>
            <legend><span class="number">1</span> Action Result</legend>
            <?php  echo $result; ?>
            <br /><br />
            <legend><span class="number">2</span> Device Status</legend>
            <label for="statusreadonly">Relay states:</label>
            <input type="text" id="statusreadonly" readonly name="statusreadonly" value="<?php echo file_get_contents('./devices/' . $device . "_sta"); ?>">
            </fieldset>
            <fieldset>
                <legend><span class="number">3</span> Device Action</legend>
                <label for="channel">Channel:</label>
                <select id="channel" name="channel">
                    <option value="1" selected>1 - One</option>
                    <option value="2">2 - Two</option>
                    <option value="3">3 - Three</option>
                    <option value="4">4 - Four</option>
                    <option value="5">5 - Five</option>
                    <option value="6">6 - Six</option>
                    <option value="7">7 - Seven</option>
                    <option value="8">8 - Eight</option>
                    <option value="X">All Channels</option>
                </select>      
                <label for="action">Action:</label>
                <select id="action" name="action">
                    <option value="1">Pull - Close relay</option>
                    <option value="2" selected>Release - Open Relay</option>
                </select>      
                <label for="timeout">Timeout:</label>
                <select id="timeout" name="timeout">
                    <option value="">No timeout</option>
                    <option value="*" selected>Jog for 500ms</option>
                    <option value=":01">1 sec</option>
                    <option value=":05">5 sec</option>
                    <option value=":10">10 sec</option>
                    <option value=":60">1 min</option>
                </select>      
            </fieldset>
            <input type="submit" name="submit" value="Apply" /> <input type="submit" name="submit" value="Query Status" />
        </form>
        </div>
    </body>
</html>