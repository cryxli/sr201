<?php error_reporting(E_ERROR | E_PARSE); ?>
<html>
<header><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>sr201 Contol</title>
    <link rel="stylesheet" type="text/css" href="form.css">
</header>

<body>
    <div class="form-style-5">
    <form action="device.php" method="post">
        <fieldset>
        <legend><span class="number">1</span> Device Info</legend>
        <label for="serial">Serial:</label>
        <input type="text" id="serial" name="serial" placeholder="Device serial *" value="<?php echo $_COOKIE['serial'] ?>">
        <label for="password">Password</label>
        <input type="password" name="password" placeholder="Device password *" value="<?php echo $_COOKIE['password'] ?>">
        </fieldset>
        <fieldset>
            <legend><span class="number">2</span> Device Action</legend>
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