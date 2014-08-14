<?php
/*
* Este arquivo pode ser um Web Service Restful
*/
if(isset($_POST["nome"]) && isset($_POST["senha"])){
	if($_POST["nome"]=="teste" && $_POST["senha"]=="teste"){
		echo '[{"sucess":"1"}]';
	}else{
		echo '[{"sucess":"0"}]';
 	}
}else{
	echo '[{"sucess":"0"}]';
}