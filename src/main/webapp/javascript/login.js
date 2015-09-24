/*
 * Functions for the login page.  Depends on main.js, jquery, and jquery-ui.
 */

$(window).ready(function() {
    alignLabels($('#login_form'));
    $("input:text:visible:first").focus();
});

function login(ref) {
    if (!$('#j_username').val().length) {
        textDialog('Please provide a user name to sign in.','User Name Required');
        return false;
    }
    if (!$('#j_password').val().length) {
        textDialog('Please provide a password to sign in.','Password Required');
        return false;
    }
    return true;
}