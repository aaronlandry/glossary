/*!
 * util.js requires jquery and jquery-ui.
 */

// HACK
function isIE8() {
    return ((document.documentMode || 100) < 9);
}
function isChrome() {
    return IS_CHROME;
}
function isFirefox() {
    return IS_FF;
}
var IS_FF = /mozilla/.test(navigator.userAgent.toLowerCase());
var IS_CHROME = /chrome/.test(navigator.userAgent.toLowerCase());
var isIE = (window.ActiveXObject || "ActiveXObject" in window) ? true : false;

var DIALOG_ID = 0;
var DIALOG_EFFECT = "drop"; // drop

/* must be local URL */
function urlDialog(myUrl,title, passedConfig) {
    if (!passedConfig) passedConfig = { };
    var config = {
        autoOpen: false,
        closeOnEscape: !passedConfig.disallowClose, 
        position: {my:'center',at:'center'},
        minHeight:75,
        maxHeight:$(window).height()-50,
        //maxWidth: Math.min($(window).width()-150,1200),
        //resizable:!passedConfig.extend,
        width:'auto',
        title:!!title ? title : 'No Title'
    };
    if (DIALOG_EFFECT && DIALOG_EFFECT.length) {
        config.show = DIALOG_EFFECT;
        config.close = DIALOG_EFFECT;
        config.hide = DIALOG_EFFECT;
    }
    for (var attr in passedConfig) config[attr] = passedConfig[attr];
    config.title = config.title.toUpperCase();
    var $div = $("<div>Loading...</div>").addClass('dialog-source');
    if (!!config.customDivId) {
        $div.attr('id',config.customDivId);
    }
    var $dialog = $div.pxDialog(config);
    var namespace = 'dialog_'+(DIALOG_ID++);
    $div.loginAwareLoad(myUrl, function(responseText,responseStatus,XMLHttpRequest) {
        $dialog.dialog('open').bind('dialogclose', function() {
            $(document).off('keydown.'+namespace);
            $dialog.dialog('destroy').remove();
        });
        $dialog.initializeDialog(passedConfig);
        if (!checkLoadResponse($div,XMLHttpRequest)) {
            return;
        }
        var $parent = $dialog.parents('div.ui-dialog');
        $parent.on('keypress.'+namespace,function (event) {
            if (!config.noCloseOnEnter && event.keyCode === $.ui.keyCode.ENTER) {
                $dialog.dialog(config).dialog("close");
                event.preventDefault();
                event.stopPropagation();
            }
            else if (!config.disallowClose && event.keyCode === $.ui.keyCode.ESCAPE) {
                $dialog.dialog("close");
                event.preventDefault();
                event.stopPropagation();
            }
        });
        if (config.disallowClose) {
            $parent.find(".ui-dialog-titlebar-close").hide();
        }
        alignLabels($dialog);
        if (!!config.onload) {
            config.onload($parent);
        }
    });
}

var LOCKED = false;
var LOCKED2 = false;
var LOCKED3 = false;
var LOCKED4 = false;

function submittableDialog(myUrl,title,id,passedConfig) {
    if (LOCKED) { return; }
    LOCKED = true;
    if (!passedConfig) passedConfig = { };
    var config = {
        autoOpen: false,
        closeOnEscape: !passedConfig.disallowClose, 
        position: {my:'center',at:'center'},
        minHeight:75,
        maxHeight:$(window).height()-50,
        //maxWidth: Math.min($(window).width()-150,1200),
        //resizable:!passedConfig.extend,
        width:'auto',
        title:!!title ? title : 'No Title'
    };
    if (DIALOG_EFFECT && DIALOG_EFFECT.length) {
        config.show = DIALOG_EFFECT;
        config.close = DIALOG_EFFECT;
        config.hide = DIALOG_EFFECT;
    }
    for (var attr in passedConfig) config[attr] = passedConfig[attr];
    config.title = config.title.toUpperCase();
    var $div = $("<div>Loading...</div>").addClass('dialog-source');
    if (!!config.customDivId) {
        $div.attr('id',config.customDivId);
    }
    config.buttons = {
        "Cancel": { text:'Cancel', id: 'cancel_'+namespace, click: function() { $dialog.dialog("close"); } },
        "Submit": {
            click: function() { 
                var $form = $div.find('form');
                $form.trigger('dobeforesubmit');  // ALLOW BINDING OF SPECIAL HANDLERS TO THE FORM
                var $attrs = $form.find(':input');
                $div.find('div.entity-form-error-div').remove();
                $form.find('.error').hide().find('div.error-contents').html('');
                $attrs.removeClass('validation-error');
                var data = [];
                if (!!id) {
                    data[data.length] = { name:'id',value : id };
                }
                if (!!config.ids) {
                    data[data.length] = { name:'ids',value : config.ids.join(',') };
                }
                if (!!config.arguments) {
                    var parts = config.arguments.split('&');
                    for (var i = 0; i < parts.length; i++) {
                        var hsh = {};
                        hsh.name = parts[i].split('=')[0];
                        hsh.value = parts[i].split('=')[1];
                        data[data.length] = hsh;
                    }
                }
                var $dncos = $form.find('input[name="do_not_close_on_submit"]');
                var do_not_close_on_submit = $dncos.length>0 && parseInt($dncos.val()) === 1;
                //if (!!id) data.id = id;
                var ajaxArgs = {
                    url: $($form.get(0)).attr('action'),
                    type:'POST',
                    dataType:'json',
                    success: function(returnData) {
                        if (returnData.validationError === true) {
                            for (var attr in returnData) {
                                attr = attr.replace(/\.\[/g,'[');
                                var fattr = attr;
                                if (!!fattr && fattr.charAt(attr.length-1) === ".") {
                                    fattr = fattr.substring(0,attr.length-1);
                                }
                                var $attr = $form.find('[name="'+fattr+'"]');
                                if ($attr.length) {
                                    $attr.addClass('validation-error');
                                }
                                var cattr = attr.replace(/[\[\]\.]/g, '-');
                                cattr = cattr.replace(/\-\d+\-$/,'');
                                var $edev = $form.find('div.error-div-'+cattr).addClass('error-div').last().show().find('div.error-contents');
                                if (!$edev.length) {
                                    var $vdiv = $attr.closest('.validatable');
                                    if (!!$vdiv.length) {
                                        var $newdev = $('<div></div>').addClass('error error-div error-div-' + cattr);
                                        var $span = $('<div></div>').addClass('error-contents');
                                        $newdev.append($span);
                                        $vdiv.append($newdev);
                                        $newdev.show();
                                        $edev = $span;
                                    }
                                }
                                if ($edev.length) {
                                    var msg = returnData[attr];
                                    if (!msg || msg.toUpperCase() === 'NULL') {
                                        debug('No Error Message for ' + attr + '.')
                                        continue;
                                    }
                                    if ($edev.html().length > 0) {
                                        $edev.append(", " + msg);
                                    }
                                    else $edev.html(msg);
                                }
                                else {
                                    debug('Unrecorded error in form submission: ' + attr + ' - ' + returnData[attr]);
                                }
                            }
                            var $error_div = $('<div></div>').addClass('entity-form-error-div').addClass('error').css({'padding-top':'10px'}).show();
                            $error_div.html('There was an issue submitting your form' + (!!returnData[''] ? (' ['+returnData['']+']') : '') + '. Please correct these problems and try again.');
                            $form.after($error_div);
                            $form.find('input[name="do_not_close_on_submit"]').val(1);
                        }
                        else if (returnData.visibleException === true) {
                            var $error_div = $('<div></div>').addClass('entity-form-error-div').addClass('error').css({'padding-top':'10px'}).show();
                            $error_div.html(returnData.exceptionMsg);
                            $error_div.find('span.disable-me').remove();
                            $form.after($error_div);
                            $form.find('input[name="do_not_close_on_submit"]').val(1);
                        }
                        else if (returnData.exception === true) {
                            adminError(returnData.exceptionMsg,"Intercepted Exception");
                            var $error_div = $('<div></div>').addClass('entity-form-error-div').addClass('error').css({'padding-top':'10px'}).show();
                            $error_div.html('An unknown error occurred submitting your form.');
                            $form.after($error_div);
                            $form.find('input[name="do_not_close_on_submit"]').val(1);
                        }
                        else {
                            if (!do_not_close_on_submit) $dialog.dialog("close");
                            if (!config.noSuccessMessage && returnData.successMessage) {
                                textDialog(returnData.successMessage,'Success');
                            }
                            returnData.$ref = passedConfig.ref;  // Not always provided ....
                            if (config.callback) {
                                config.callback(returnData);
                            }
                            if (returnData.callback) {
                                var f;
                                eval("f = " + returnData.callback);
                                f(returnData,$dialog);
                            }
                        }
                    },
                    error: function(data) {
                        if (data.status && data.status === 403) {
                            onJsonLogout();
                            return;
                        }
                        adminError(data.responseText,data.exception===true?"Intercepted Exception" : "Unintercepted Exception");
                        var $error_div = $('<div></div>').addClass('entity-form-error-div').addClass('error');
                        $error_div.html('An unknown error occurred submitting your form.');
                        $form.after($error_div);
                        $form.find('input[name="do_not_close_on_submit"]').val(1);
                    }
                };
                ajaxArgs.data = data.concat($form.serializeArray());
                $.ajax(ajaxArgs);
            },
            text: 'Submit',
            id: 'submit_'+namespace
        }
    };
    var $dialog = $div.pxDialog(config);
    var namespace = 'dialog_'+(DIALOG_ID++);
    $div.loginAwareLoad(myUrl, function(responseText,responseStatus,XMLHttpRequest) {
        $dialog.dialog('open').bind('dialogclose', function() {
            $(document).off('keydown.'+namespace);
            $dialog.dialog('destroy').remove();
        });
        $dialog.initializeDialog(passedConfig);
        if (!checkLoadResponse($div,XMLHttpRequest)) {
            return;
        }
        var $parent = $dialog.parents('div.ui-dialog');
        $parent.on('keypress.'+namespace,function (e) {
            if (e.keyCode === $.ui.keyCode.ENTER) {
                if (!$parent.find('textarea:focus').length) {
                    if ($('#submit_'+namespace).length) {
                        if ($parent.find('div.ui-dialog-buttonpane').is(':visible')) {
                            $('#submit_'+namespace).trigger('click');
                        }
                    }
                    else $dialog.dialog("close");
                    e.preventDefault();
                    e.stopPropagation();
                }
            }
            else if (!config.disallowClose && e.keyCode === $.ui.keyCode.ESCAPE) {
                $dialog.dialog("close");
                e.preventDefault();
                e.stopPropagation();
            }
        });
        $parent.find('form').on('submit',function() { return false; });
        $parent.find('#submit_'+namespace).attr('title','Submit').removeClass().off('mouseover').addClass('ui-dialog-submit').addClass('link-button link-button-primary');
        $parent.find('#cancel_'+namespace).attr('title','Cancel').removeClass().off('hover').addClass('ui-dialog-cancel link-button link-button-secondary');
        if (config.disallowClose) {
            $parent.find(".ui-dialog-titlebar-close").hide();
        }
        alignLabels($dialog);
        if (!!config.onload) {
            config.onload($parent);
        }
    });
    setTimeout(function() { LOCKED = false; }, 100);
}

function textDialog(text,title,passedConfig) {
    if (LOCKED2) { return; }
    LOCKED2 = true;
    if (!passedConfig) passedConfig = { };
    var p = {my:'center',at:'center'};
    if (passedConfig.sourceEvent) {
        p = {my:'left',at:'left',of:passedConfig.sourceEvent};
    }
    var config = {
        autoOpen: false,
        closeOnEscape: !passedConfig.disallowClose, 
        position: p,
        height:'auto',
        maxHeight:$(window).height()-50,
        //maxWidth: Math.min($(window).width()-150,1200),
        minHeight:75,
        //resizable:!passedConfig.extend,
        width:'auto',
        title:!!title ? title : 'No Title'
    };
    if (DIALOG_EFFECT && DIALOG_EFFECT.length) {
        config.show = DIALOG_EFFECT;
        config.close = DIALOG_EFFECT;
        config.hide = DIALOG_EFFECT;
    }
    for (var attr in passedConfig) config[attr] = passedConfig[attr];
    config.title = config.title.toUpperCase();
    var $div = $("<div></div>").addClass('dialog-source');
    $div.html(text);
    var namespace = 'dialog_'+(DIALOG_ID++);
    var $dialog = $div.pxDialog(config);
    $dialog.dialog('open').bind('dialogclose', function() {
        $(document).off('keydown.'+namespace);
        $dialog.dialog('destroy').remove();
    }).initializeDialog(passedConfig);
    var $parent = $dialog.parents('div.ui-dialog');
    $parent.on('keypress.'+namespace,function (event) {
        if (!config.noCloseOnEnter && event.keyCode === $.ui.keyCode.ENTER) {
            $dialog.dialog(config).dialog("close");
            event.preventDefault();
            event.stopPropagation();
        }
        else if (!config.disallowClose && event.keyCode === $.ui.keyCode.ESCAPE) {
            $dialog.dialog("close");
            event.preventDefault();
            event.stopPropagation();
        }
    });
    if (config.disallowClose) {
        $parent.find(".ui-dialog-titlebar-close").hide();
    }
    if (!!config.onload) {
        config.onload($parent);
    }
    setTimeout(function() { LOCKED2 = false; }, 100);
    return $parent;
}

function promptDialog(text,title,verifyFunction,submitFunction,passedConfig) {
    if (LOCKED3) { return; }
    LOCKED3 = true;
    if (!passedConfig) passedConfig = { };
    var p = {my:'center',at:'center'};
    if (passedConfig.sourceEvent) {
        p = {my:'left',at:'left',of:passedConfig.sourceEvent};
    }
    var config = {
        autoOpen: false,
        closeOnEscape: !passedConfig.disallowClose, 
        position: p,
        maxHeight:$(window).height()-50,
        //maxWidth: Math.min($(window).width()-150,1200),
        minHeight:75,
        //resizable:!passedConfig.extend,
        width:'auto',
        height:'auto',
        title:!!title ? title : 'No Title'
    };
    if (DIALOG_EFFECT && DIALOG_EFFECT.length) {
        config.show = DIALOG_EFFECT;
        config.close = DIALOG_EFFECT;
        config.hide = DIALOG_EFFECT;
    }
    for (var attr in passedConfig) config[attr] = passedConfig[attr];
    config.title = config.title.toUpperCase();
    var $div = $("<div></div>").addClass('dialog-source');;
    var $form = $("<form onsubmit='return false;' class='prompt-form' name='prompt-form'></form>");
    $div.append($form);
    $form.html(text);
    var namespace = 'dialog_'+(DIALOG_ID++);
    config.buttons = {
        "Cancel": { text:'Cancel', id: 'cancel_'+namespace, click: function() { $dialog.dialog("close"); } },
        "Submit": {
            click: function() { 
                try {
                    var f = $div.find('form.prompt-form').get(0);
                    if (verifyFunction(f,$dialog)) {
                        if (submitFunction(f,$dialog)) {
                            $dialog.dialog("close");
                        }
                    }
                    
                }
                catch(ex) {
                    if (!adminError(ex.message,"Error Performing Request"))
                        textDialog("There was a problem performing your request.  It has been canceled.","Internal Error");
                    $dialog.dialog("close");
                }
            },
            text: 'Submit',
            id: 'submit_'+namespace
        }
    };
    var $dialog = $div.pxDialog(config);
    $dialog.dialog('open').bind('dialogclose', function() {
        $(document).off('keydown.'+namespace);
        $dialog.dialog('destroy').remove();
    }).initializeDialog(passedConfig);
    var $parent = $dialog.parents('div.ui-dialog');
    $parent.on('keypress.'+namespace,function (event) {
        if (event.keyCode === $.ui.keyCode.ENTER) {
            if (!$parent.find('textarea:focus').length) {
                $('#submit_'+namespace).click();
                event.preventDefault();
                event.stopPropagation();
            }
        }
        else if (!config.disallowClose && event.keyCode === $.ui.keyCode.ESCAPE) {
            $dialog.dialog("close");
            event.preventDefault();
            event.stopPropagation();
        }
    });
    $parent.find('#submit_'+namespace).attr('title','Submit').removeClass().addClass('ui-dialog-submit').addClass('link-button link-button-primary');
        $parent.find('#cancel_'+namespace).attr('title','Cancel').removeClass().addClass('link-button link-button-secondary');
    if (config.disallowClose) {
        $parent.find(".ui-dialog-titlebar-close").hide();
    }
    if (!!config.onload) {
        config.onload($parent);
    }
    setTimeout(function() { LOCKED3 = false; }, 100);
    return $parent;
}

function confirmationDialog(text,title,callback,passedConfig) {
    if (LOCKED4) { return; }
    LOCKED4 = true;
    if (!passedConfig) passedConfig = { };
    var config = {
        autoOpen: false,
        closeOnEscape: !passedConfig.disallowClose, 
        position: {my:'center',at:'center'},
        maxHeight:$(window).height()-50,
        //maxWidth: Math.min($(window).width()-150,1200),
        minHeight:75,
        //resizable:!passedConfig.extend,
        width:'auto',
        height:'auto',
        title:!!title ? title : 'No Title'
    };
    if (DIALOG_EFFECT && DIALOG_EFFECT.length) {
        config.show = DIALOG_EFFECT;
        config.close = DIALOG_EFFECT;
        config.hide = DIALOG_EFFECT;
    }
    for (var attr in passedConfig) config[attr] = passedConfig[attr];
    config.title = config.title.toUpperCase();
    var $div = $("<div></div>").addClass('dialog-source');;
    $div.html(text);
    var namespace = 'dialog_'+(DIALOG_ID++);
    config.buttons = {
        "Cancel": { text:'Cancel', id: 'cancel_'+namespace, click: function() { $dialog.dialog("close"); } },
        "Continue": {
            click: function() { 
                try {
                    callback({});
                    $dialog.dialog("close");
                }
                catch(ex) {
                    if (!adminError(ex.message,"Error Performing Request"))
                        textDialog("There was a problem performing your request.  It has been canceled.","Internal Error");
                    $dialog.dialog("close");
                }
            },
            text: 'Continue',
            id: 'continue_'+namespace
        }
    };
    var $dialog = $div.pxDialog(config);
    $dialog.dialog('open').bind('dialogclose', function() {
        $(document).off('keydown.'+namespace);
        $dialog.dialog('destroy').remove();
    }).initializeDialog(passedConfig);
    var $parent = $dialog.parents('div.ui-dialog');
    $parent.on('keypress.'+namespace,function (event) {
        if (event.keyCode === $.ui.keyCode.ENTER) {
            if (!$parent.find('textarea:focus').length) {
                $('#continue_'+namespace).click();
                event.preventDefault();
                event.stopPropagation();
            }
        }
        else if (!config.disallowClose && event.keyCode === $.ui.keyCode.ESCAPE) {
            $dialog.dialog("close");
            event.preventDefault();
            event.stopPropagation();
        }
    });
    $parent.find('#continue_'+namespace).attr('title','Continue').removeClass().addClass('ui-dialog-submit').addClass('link-button link-button-primary');
        $parent.find('#cancel_'+namespace).attr('title','Cancel').removeClass().addClass('link-button link-button-secondary');
    if (config.disallowClose) {
        $parent.find(".ui-dialog-titlebar-close").hide();
    }
    if (!!config.onload) {
        config.onload($parent);
    }
    setTimeout(function() { LOCKED4 = false; }, 100);
    return $parent;
}

function popupError(msg,title) {
    var $div = $('<div></div>').addClass('dialog-source');;
    $div.append(msg);
    if ($div.find('.exception-wrapper').length === 0) {
        $div.wrap('<div class="exception-wrapper"></div>');
    }
    textDialog($div, !!title ? title : 'An Error Occurred');
}

function adminError(msg,title) {
    if (!window.userType || userType === BOTTOMLINE_USER_TYPE) {    // BOTTOMLINE USER
        // show message
        var $div = $('<div></div>');
        $div.append(msg);
        if ($div.find('.admin-only').length === 0) {
            $div.prepend('<span class=admin-only>This message is only visible to admins!</span><br/>');
        }
        if ($div.find('.exception-wrapper').length === 0) {
            var $wrapper = $('<div></div>').addClass('exception-wrapper').append($div);
            $div = $wrapper;
        }
        textDialog($div,!!title?title:'An Error Occurred');
        return true;
    }
    return false;
}

function splitParams(paramString) {
    var p = {};
    var raw = paramString.split('&');
    for (var i = 0; i < raw.length; i++) {
        var inner = raw[i].split('=');
        p[inner[0]] = inner[1];
    }
    return p;
}

function checkLoadResponse($div,XMLHttpRequest) {
    switch (XMLHttpRequest.status) {
        case 200:
            return true;
        case 403:
            onJsonLogout();
            return false;
        default:
            if ($div) $div.remove();
            if (!adminError(XMLHttpRequest.responseText,XMLHttpRequest.exception===true?"Intercepted Exception" : "Unintercepted Exception")) {
                textDialog('<div class=exception-wrapper><div class=exception-custom>An unknown error caused your request to be denied.</div></div>','Unknown Error');
            }
            return false;
    }
}

function onJsonLogout() {
    // SET UP REDIRECTION AFTER RE-LOGIN
    //window.location = '/Superiad/index.do';
}

function nyi(msg) {
    var $img = $('<img />').attr('src','/Superiad/images/under-construction.jpg');
    var $div = $('<div />').css({'width':'500px'}).addClass('left');
    $div.append($('<div />').addClass('left').append($img));
    $div.append($('<div />').css({'width':'300px','padding-left':'20px','padding-top':'20px'}).addClass('left')
        .html('This page is currently unavailable.  It may be still in development, or it may have been temporarily disabled for this build.  Check back soon!'));
    textDialog($div,'Under Construction');
}

/* FUNCTIONS SHARED BETWEEN CRUD AND LOGIN */

function alignLabels($form) {
    alignElements($form.find('label,div.pst-q-label,div.element-label,div.composite-label-left'));
}

function alignElements($els) {
    var maxwidth = 0;
    $els.each(function() { if (!$(this).closest('.inner-holder,.do-not-align,.ms-label,.floating-checkboxes').length) { var thiswidth = $(this).width(); if (thiswidth>maxwidth) maxwidth=thiswidth; } });
    $els.each(function() { if (!$(this).closest('.inner-holder,.do-not-align,.ms-label,.floating-checkboxes').length) { $(this).css({'min-width':maxwidth+'px','text-align':'right'}); }});
}

// FOR IE8
if (typeof String.prototype.trim !== 'function') {
   String.prototype.trim= function(){
       return this.replace(/^\s+|\s+$/g, '');
   };
}

if (typeof String.prototype.endsWith !== 'function') {
    String.prototype.endsWith = function(suffix) {
        return this.indexOf(suffix, this.length - suffix.length) !== -1;
    };
}

String.prototype.truncate=function(max){if (this.length <= max) return this;  return this.substring(0, max-3)+'...'; };

function showAndPositionTimer(txt) { 
   $('.timer').html(!!txt?txt:'&nbsp;&nbsp;Loading Request...').show().centerMe();
   try { $('.timer').css('top',$('.timer').position().top-200); } catch(e) { }
}

function hideTimer() {
    $('.timer').hide();
}

function hideAllSubmenus(){
    $('.cont-submenu').fadeOut(200);
}

function displaySubmenu(submenuId, targetTab){
    hideAllSubmenus();
    var submenu = $('#' + submenuId),p = $(targetTab).position().left + 20;
    submenu.css({'left':p + 'px','z-index':10000}).fadeIn(200);
}

function loadScript(src,callback) {
    var script = document.createElement("script");
    script.type = "text/javascript";
    if(callback) script.onload=callback;
    document.getElementsByTagName("head")[0].appendChild(script);
    script.src = src;
}

function mailTo(url) {
    // I have often experienced Firefox errors with protocol handlers
    // so better be on the safe side.
    try {
        var mailer = window.open(url, 'Mailer');
    } 
    catch(e) {
        console.log('There was an error opening a mail composer.', e);
    }
    setTimeout(function() {
        // This needs to be in a try/catch block because a Security 
        // error is thrown if the protocols doesn't match
        try {
            // At least in Firefox the locationis changed to about:blank
            if(mailer.location.href === url || mailer.location.href.substr(0, 6) === 'about:') {
                mailer.close();
            }
        } 
        catch(e) {
            console.log('There was an error opening a mail composer.', e);
        }
    }, 1000);
};

function debug(str){
    // IN IE8, THE CONSOLE DOES NOT EXIST UNTIL OPENED
    if (window.console && window.console.log) {
        window.console.log(str);
    }
    else if (window.opera && window.opera.postError) {
        window.opera.postError(str);
    }
    return true;
}

function doLogout() {
    $.ajax({url:'j_spring_security_logout',type:'post', success:function() { onJsonLogout(); }});
}