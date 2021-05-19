<?php
/* Ad hoc functions to make the examples marginally prettier.*/
/* Mostly copied from the client library base examples. */
function isWebRequest() {
  return isset($_SERVER['HTTP_USER_AGENT']);
}

function pageHeader($title) {
  $ret = "";
  if (isWebRequest()) {
    $ret .= "<!doctype html>
    <html>
    <head>
      <title>" . $title . "</title>
      <link href='styles/style.css' rel='stylesheet' type='text/css' />
    </head>
    <body>\n";
    $ret .= "<header><h1>" . $title . "</h1></header>";
  }
  return $ret;
}

function pageFooter() {
  $ret = "";
  if (isWebRequest()) {
    $ret .= "</html>";
  }
  return $ret;
}

function missingClientSecretsWarning() {
  $ret = "";
  if (isWebRequest()) {
    $ret = "
      <h3 class='warn'>
        Warning: You need to set Client ID, Client Secret and Redirect URI on
        the client_secrets.json file. You can get these from the
        <a href='http://developers.google.com/console'>Google API console</a>
      </h3>";
  } else {
    $ret = "Warning: You need to set Client ID, Client Secret and Redirect URI";
    $ret .= " on the client_secrets.json file. You can get these from:\n";
    $ret .= "http://developers.google.com/console";
  }
  return $ret;
}
