<?php

/*
mysql_connect("localhost","distsys","intensiviLerngruppe");
mysql_select_db("distsys");
 
$q=mysql_query("SELECT * FROM Flags");

mysql_connect("host","username","password");
mysql_select_db("PeopleData");
 
$q=mysql_query("SELECT * FROM people WHERE birthyear>'".$_REQUEST['year']."'");

while($e=mysql_fetch_assoc($q))
        $output[]=$e;
 
print(json_encode($output));
 
mysql_close();

*/


//could put this stuff in a separate file
define('HOST','localhost');
define('USER','distsys');
define('PASS','intensiviLerngruppe');
define('DB','distsys');

$con = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect');



if($_SERVER['REQUEST_METHOD']=='GET'){
	
	//$id  = $_GET['id'];
	
	//if we had the "defines" in another file we would need this
	//require_once('dbConnect.php');
	
	$sql = "SELECT * FROM Flags WHERE flagID='1'";
	
	$r = mysqli_query($con,$sql);
	
	$res = mysqli_fetch_array($r);
	
	$result = array();
	
	array_push($result,array(
		"flagID"=>$res['flagID'],
		"userName"=>$res['userName'],
		"gpsCoordinates"=>$res['gpsCoordinates'],
		"categoryName"=>$res['categoryName'],
		"date"=>$res['date'],
		"content"=>$res['content']
		)
	);
	
	echo json_encode(array("result"=>$result));
	
	mysqli_close($con);
		
}

?>