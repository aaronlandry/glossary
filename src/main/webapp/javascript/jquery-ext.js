// extends $.load to react to login expiration
$.fn.printMe = function() {
    $('.print').removeClass('print');
    var w = $(this).width();
    $(this).addClass('print').width(w);
    window.print();
};
$.fn.linkMe = function() {
    $(this).each(
        function() {
            var $a = $(this);
            $a.attr('href', "javascript:void(false)").on('click',
                function() {
                    var termID = $a.attr('termID');
                    var onload = function($ref) {
                        $ref.find('a.term-link').linkMe();
                    }
                    urlDialog("/Superiad/CRUD/Read/Term/" + termID + ".do", 'View Term',{onload:onload});
                }
            );
            /*
            var tn = $a.get(0).nextSibling;
            if (tn === null || tn.nodeValue === null) {
                return;
            }
            if (tn.nodeValue.substring(0, 2) === 's ') {
                tn.nodeValue = tn.nodeValue.replace('s ', ' ');
                $a.append('s');
            }
            else if (tn.nodeValue.substring(0, 2) === 's.') {
                tn.nodeValue = tn.nodeValue.replace('s.', '.');
                $a.append('s');
            }
            else if (tn.nodeValue.substring(0, 2) === 's,') {
                tn.nodeValue = tn.nodeValue.replace('s,', ',');
                $a.append('s');
            }
            else if (tn.nodeValue.substring(0, 2) === 's;') {
                tn.nodeValue = tn.nodeValue.replace('s;', ';');
                $a.append('s');
            }
            else if (tn.nodeValue.substring(0, 2) === 's:') {
                tn.nodeValue = tn.nodeValue.replace('s:', ':');
                $a.append('s');
            }
            */
        }
    );
};
$.fn.loginAwareLoad = function(a,b,c,d) {
    var $ref = $(this);
    var onload = null;
    var TID = null;
    if (!$('.timer').is(':visible')) {
        TID = setTimeout(function() { showAndPositionTimer(); }, 300);
    }
    if (!b) {
        onload = function() {
            if (TID) clearTimeout(TID);
            hideTimer();
            if ($ref.find('form#login_form').length) {
                onJsonLogout();
                return;
            }
        };
    }
    else {
        onload = function(x,y,z) { 
            if (TID) clearTimeout(TID);
            hideTimer();
            if ($ref.find('form#login_form').length) {
                onJsonLogout();
                return;
            }
            $ref.pxTooltip();
            b(x,y,z);
        };
    }
    $ref.load(a,onload,c);
};

// extends ajax to react to login expiration
$.loginAwareAjax = function(args) {
    var origSuccess = args.success;
    args.success = function(data) {
        if (typeof data === 'string' && data.indexOf('form#login_form') !== -1) {
            onJsonLogout();
            return;
        }
        if (origSuccess) origSuccess(data);
    };
    $.ajax(args);
};

$.fn.selectOptionsFromString = function(str) {
    var $s = $(this);
    if (!str) {
        return;
    }
    var items = str.split(",");
    for (var i = 0; i < items.length; i++) {
        var val = items[i];
        $s.find('option[value=' + val + ']').prop('selected',true);
    }
};

// add hash values to a select list
$.fn.addHashToList = function(hash) {
    var $list = $(this);
    for (var key in hash) {
//        $list.append(new Option(hash[key], key)); // BREAKS IN IE 8
        $list.append($('<option />').val(key).text(hash[key]));
    }
    $list.trigger('change');
    return $list;
};

// add array of hash values to a select list
$.fn.addArrayToList = function(arr) {
    var $list = $(this);
    $.each(arr, function(index, item) {
        // $list.append(new Option(item.label, item.value)); breaks in IE8!!!
        $list.append($('<option />').val(item.value).text(item.label));
    });
    $list.trigger('change');
    return $list;
};

// sort select by label
$.fn.sortList = function() {
    var $list = $(this);
    $list.html($list.find('option'), $list).sort(
        function($a,$b) {
            if ($a.val() === 0 || $a.val() === '') return 1;
            else if ($b.val() === 0 || $b.val() === '') return -1;
            return $a.text() > $b.text() ? 1 : ($b.text() > $a.text() ? -1 : 0);
        }
    );
    return $list;
};

$.fn.initializeDialog = function(passedConfig) {
    if (!passedConfig) passedConfig = {};
    var $parent = $(this).closest('div.ui-dialog');
    if ($parent.find('.disable-me').length) {
        $parent.find('div.ui-dialog-buttonpane').remove();
    }
    var $dialog = $(this);
    if (passedConfig.sourceEvent) { 
        //alert('here')
        //$dialog.dialog('option', 'position',{my:'left',at:'left',of:passedConfig.sourceEvent}); //[passedConfig.sourceEvent.clientX-$parent.width(),passedConfig.sourceEvent.clientY-10]); 
    }
    else { 
        //$dialog.dialog("option", "position", ['center', 'center-2%'] );
    }
    $parent.on('mouseenter',function(e) {
        // ONLY REACT TO THE TOP-MOST DIALOG
        if ($('div.ui-dialog').last().get(0) !== $parent.get(0)) {
            return;
        }
        if ($parent.find('form').length) {
            if (!$parent.find('textarea:focus,input:focus,select:focus').length) {
                $parent.find("form:not(.filter) :input:visible:enabled:first").focusWithoutScrolling($parent.find('div.ui-dialog-content'));
            }
        }
        else {
            //alert($parent.find('form').length+"\n"+$parent.html())
            $parent.focusWithoutScrolling($parent.find('div.ui-dialog-content'));
        }
        e.preventDefault();
        e.stopPropagation();
        return false;
    });
    $parent.find('textarea').on('keypress',function(e) { if (e.keyCode === $.ui.keyCode.ENTER || e.keyCode === $.ui.keyCode.ENTER) { e.preventDefault();e.stopPropagation(); } });
    $parent.trigger('mouseenter');
    $dialog.dialog('moveToTop');
    setTimeout(function() { $dialog.prepareAddresses(); $dialog.dialog('moveToTop'); },100);
    // i hate horizontal scrolling...properly size things
    $parent.find('div.ui-dialog-content').css({'overflow-x':'hidden'});
    if ($parent.find('.exception-wrapper').length) {
        var css = {
            background: '#f9f8f8',//#efefef
            color: '#e14f1c',
            border: 'none'
        };
        $parent.find('.ui-dialog-content').css(css).find('div,span,table,td,font').css(css);
    }
    $parent.find('div.ui-dialog-titlebar span.ui-icon').parent().each(function() { 
        $(this).removeClass('ui-button-icon-only').attr('title',$(this).text().charAt(0).toUpperCase() + $(this).text().substr(1)).css({'margin-left':'2px',background:'#369'});
    });
    $parent.find('div.ui-dialog-titlebar span.ui-icon-close').parent().css({'height':'20px'}).attr('title','Close').find('.ui-button-text').remove();
    $parent.find('div.ui-dialog-titlebar span.ui-icon-closethick').parent().css({'height':'20px'}).attr('title','Close').find('.ui-button-text').remove();
    $parent.find('div.ui-dialog-buttonpane').find('button').off('mouseenter').off('mouseleave').off('mousedown').off('mouseup').off('focus');
    $parent.pxTooltip();
};

$.fn.uiMenu = function() {
    var $dbm = $(this);
    $dbm.menu({ position: { my: "left top", at: "left-3 top+29" } }).find('li').width(175);
    $dbm.find('li.first-item').find('span.ui-icon-carat-1-e').removeClass('ui-icon-carat-1-e').addClass('ui-icon-carat-1-s');
    $(document).on('click',function() { $dbm.menu("collapse"); });
    return $(this);
};

$.fn.setOnSelectFunction = function(func) {
    var onSelectFunction = function() { /* DO NOTHING */};
    eval("onSelectFunction = function($input,id,label) { " + func +  "($input,id,label); }");
    $(this).on('change',
        function(e) {
            var $opt = $(this).find(':selected');
            onSelectFunction($(this),$opt.val(),$opt.text());
        }
    );
};

$.fn.defaultSingleAutoComplete = function(lookupUrl,settings) {
    var $label = $(this);
    var $label_copy = $(this).parent().find('input.ac-label-copy');
    var $value = $(this).parent().find('input.ac-value');
    if (!settings) settings= {};
    var config = {data:{}};
    var onSelectFunction = function() { /* DO NOTHING */};
    if (settings.onSelectFunction) {
        eval("onSelectFunction = function($input,id,label) { " + settings.onSelectFunction +  "($input,id,label); }");
        $value.on('change',function() { onSelectFunction($value,$value.val(),$label.val()); });   // here so that we can call trigger('change') outside autocomplete
    }
    if (typeof(lookupUrl) === 'function') {
        config.url = lookupUrl;
    }
    else if (lookupUrl.indexOf('function') === 0) {
        eval('config.url = ' + lookupUrl);
    }
    else {
        if (lookupUrl.indexOf("?") !== -1) {
            var parts = lookupUrl.split("\?");
            config.url = parts[0];
            config.data = splitParams(parts[1]); 
        }
        else config.url = lookupUrl;
    }
    var $dialog = $label.closest('.ui-dialog');
    $label.autocomplete({
        //minLength: MINIMUM_AUTOCOMPLETE,
        autoFocus: false,
        delay:250,
        // store label and label_copy so that we can evaluate manual edits on blur
        open:function(event,ui) {
            if ($dialog.length) {
                $('ul.ui-autocomplete').css({'z-index':parseInt($dialog.css('z-index')) + 1});
            }
        },
        response: function(event,ui) {
            if ($dialog.length) {
                $('ul.ui-autocomplete').css({'z-index':parseInt($dialog.css('z-index')) + 1});
            }
        },
        select: function(event, ui) { $label.val(ui.item.label); $label_copy.val(ui.item.label); $value.val(ui.item.value).trigger('change'); onSelectFunction($label,ui.item.value,ui.item.label);event.preventDefault();return false; },
        focus: function(event, ui) { $label.val(ui.item.label); $label_copy.val(ui.item.label); $value.val(ui.item.value).trigger('change'); return false; },
        close: function() { setTimeout(function() { $label.attr('isOpen',0); }, 100); },
        source: function(request,response) {
            if (!request.term.length) return;
            if (request.term.length < MINIMUM_AUTOCOMPLETE && request.term !== '*') return;
            config.data.term = request.term;
            $.ajax({
                url: typeof(config.url) === 'function' ? config.url() : config.url,
                dataType: 'json',
                data: config.data,
                success: function(data) {
                    onSelectFunction($label,null);
                    if (typeof data === 'string') {
                        adminError(data,"Unintercepted Exception");
                        response([]);
                    }
                    else {
                        $label.attr('isOpen',1);
                        response(data);
                        
                        // HANDLE CIRCUMSTANCE WHERE AUTO-COMPLETE SEEMS TO IGNORE CLICKS AFTER THE FIRST
                        $('ul.ui-autocomplete>li').on('click',function() { $label.autocomplete( "close" ); });
                    }
                },
                error: function(data) {
                    onSelectFunction($label,null);
                    if (!checkLoadResponse(null,data)) {
                        return;
                    }
                    adminError(data.responseText,data.exception===true?"Intercepted Exception" : "Unintercepted Exception");
                    response([]);
                }
            });
        },
        // evaluated onblur
        change: function(event,ui) {
            // clear $value of the $label has been manually edited
            if ($label_copy.length && $label.val() !== $label_copy.val()) $value.val(0).trigger('change');
            // In the case of manual entry, automatically "discover" if it has been previously selected
            var autocomplete = $label.data("ui-autocomplete");
            var matcher = new RegExp("^" + $.ui.autocomplete.escapeRegex($(this).val()) + "$", "i");
            autocomplete.widget().children(".ui-menu-item").each(function() {
                var item = $(this).data("ui-autocomplete-item");
                if (matcher.test(item.label || item.value || item)) {
                    autocomplete.selectedItem = item;
                    return false;
                }
            });
            if (autocomplete.selectedItem) {
                autocomplete._trigger("select", event, {item: autocomplete.selectedItem});
            }
        }
    });
    $label.attr('isOpen','0');
    $label.on('keypress',function(event) {
        var keycode = (event.keyCode ? event.keyCode : event.which);
        if (keycode === 13 && $label.attr('isOpen') === '1') {
            event.preventDefault();
            event.stopPropagation();
        }
    });
    // ensure that AC is not clipped
    if ($dialog.length) {
        var $ac = $('ul.ui-autocomplete');
        if (!$ac.hasClass('positioned')) {
            $ac.insertAfter($dialog).css({'z-index':250000}).addClass('positioned');
        }
        else {
            $ac.css({'z-index':250000});
        }
    }
    return $(this);
};

var $uiLastMenuRef = null;

(function($) {
    var $menu, $trigger, content, hash, currentTarget, storedEventData;
    var defaults = {
        onContextMenu: null,
        onShowMenu: null
    };
    $.fn.contextMenu = function(parts,options,stored) {
        var opts = options || {};
        storedEventData = stored;
        if (!$menu) {                                      // Create singleton menu
            $menu = $('<div id="jqContextMenu"></div>').hide().css({position:'absolute',background:'green', zIndex:'15000'})
                .appendTo('body').bind('click', function(e) {
                    e.stopPropagation();
                });
        }
        hash = hash || [];
        hash.push({
            onContextMenu: opts.onContextMenu || defaults.onContextMenu,
            onShowMenu: opts.onShowMenu || defaults.onShowMenu
        });
        var index = hash.length - 1;
        var $ref = $(this);
        var func = function(e) {
            if (!storedEventData) storedEventData = {pageX:e.pageX,pageY:e.pageY};
            $uiLastMenuRef = $ref;
            var bShowContext = (!!hash[index].onContextMenu) ? hash[index].onContextMenu(e) : true;
            if (bShowContext) {
                display(index, $ref.get(0), e, parts,opts);
            }
            try { stop(e); } catch(e) { }
            return false;
        };
        if (opts.onMouseover) {
            $(this).on('mouseenter',func);
        }
        else {
            $(this).on('contextmenu',func);
            if(opts.onClick) {
                $(this).on('click',func);
            }
        }
        return this;
    };

    function fix_menu($menu,$container) {
        // try to repair offscreen issues
        var h = $(window).height() + $(window).scrollTop();
        if (($menu.offset().top + $menu.height()) > h) {
            var nt = h - (10 + $menu.height());
            $menu.css('top',nt);
            $container.css('top',nt);
        }
        var w = $(window).width() + $(window).scrollLeft();
        if (($menu.offset().left + $menu.width()) > w) {
            var nt = w - (10 + $menu.width());
            $menu.css('left',nt);
            $container.css('left',nt);
        }
    }

    function display(index, trigger, e, raw_parts, opts) {
        var cur = hash[index];
        var parts;
        if (typeof raw_parts === "function") parts = raw_parts();
        else parts = raw_parts;
        var $container = $('<ul id=cmenu_' + cur.id + ' class="ui-menu ui-widget ui-widget-content ui-corner-all"></ul>');
        for (var i = 0; i < parts.length; i++) {
            var $link = $('<li class="cm_i_dv id=cm_i_' + i + ' ui-menu-item"><a class="ui-corner-all"><div class="menu-item-text">' + parts[i][0] + '</div></a></li>');
            $container.append($link);
        }
        $container.find('a').hover(
            function() { $(this).addClass('ui-state-focus'); },
            function() { $(this).removeClass('ui-state-focus'); }
        );
        // Send the content to the menu
        $menu.html($container);
        if (opts.onDisplayFunction) {
            opts.onDisplayFunction($container);
        }
        // if there's an onShowMenu, run it now -- must run after content has been added
        // if you try to alter the content variable before the menu.html(), older browsers have issues
        // updating the content
        if (!!cur.onShowMenu) $menu = cur.onShowMenu(e, $menu);
        $container.find('.cm_i_dv').each(
            function(counter) {
                var ord = counter;
                $(this).click(function() { $menu.hide(); parts[ord][1](); });
            }
        );
        if (opts.positionRelative) {
            var $r = opts.positionRelative;
            $menu.css({left:$r.position().left,width:$r.width()+13,top:$r.position().top+$r.height()+12}).show();
        }
        else {
            $menu.css({'left':typeof e.pageX==='undefined'?storedEventData.pageX:e.pageX,
                'top':typeof e.pageY==='undefined'?storedEventData.pageY:e.pageY,'right':'auto','width':'auto'}).show();
        }
        fix_menu($menu,$container);
        //setTimeout(function() { $(document).one('click', function() { alert('here');$menu.hide() }); }, 200);
    }

    function hide() {
        $menu.hide();
    }

    // Apply defaults
    $.contextMenu = {
        defaults : function(userDefaults) {
            $.each(userDefaults, function(i, val) {
                if (typeof val === 'object' && defaults[i]) {
                    $.extend(defaults[i], val);
                }
                else defaults[i] = val;
            });
        }
    };
    
})(jQuery);

$.fn.loadPageMenu = function(moduleID,pages) {
    var $module = $(this);
    var items = [];
    for (var i = 0; i < pages.length; i++) {
        items[i] = [];
        var pg = pages[i];
        items[i][0] = "<div style='white-space:nowrap;'>" + pg.label + '</div>';
        items[i][1] = new Function("loadPage("+moduleID+","+pg.id+");");
    }
    $module.contextMenu(function() { return items; }, {onMouseover:true,positionRelative:$module});
};

$.fn.closePageMenu = function(moduleID) {
    var $m = $(this);
    $m.on('mouseover',function() { $('#jqContextMenu').hide(); });
};

$.fn.pxtabs = function() {
  $(this).tabs();
  $(this).find('ul.ui-tabs-nav').removeClass('ui-widget-header').removeClass('ui-corner-all')
      .css({'border-bottom':'solid 1px #369'});
};

$.fn.focusWithoutScrolling = function($div){
    var x = $div.scrollLeft();
    var y = $div.scrollTop();
    this.focus();
    if (typeof y !== 'undefined') {
        $div.scrollTop(y);
        // In some cases IE9 does not seem to catch instant scrollTo request.
        setTimeout(function() { $div.scrollTop(y); }, 100);
    }
    if (typeof x !== 'undefined') {
        $div.scrollLeft(x);
        // In some cases IE9 does not seem to catch instant scrollTo request.
        setTimeout(function() { $div.scrollLeft(x); }, 100);
    }
};

$.fn.pxDatepicker = function(fmt) {
    var $dt = $(this);
    $dt.datepicker({
        changeMonth: true,
        changeYear: true,
        showButtonPanel: true,
        showOn: 'button', // prevent normal behavior (focus)
        beforeShow: function(dateText, inst) {
            $('#ui-datepicker-div').removeClass('calendar-hide').removeClass('calendar-hide-month');
        }
    });
    $dt.datepicker('option', 'dateFormat', fmt);
    $dt.datepicker('option', 'showAnim', 'slide');
    $dt.parent().find('.icon-calendar').click(function() {
        $dt.datepicker('show');
    });
    $dt.on('click', function() {
        $dt.datepicker('show');
    });
    $('#ui-datepicker-div').removeClass('calendar-hide').removeClass('calendar-hide-month');
    return $dt;
};

$.fn.isEmptyAC = function() {
    var v = $(this).val();
    if (!v) return true;
    return v === '' || parseInt(v) === 0;
};

$.fn.dependentMultiselect = function(parentID,map) {
    var $csel = $(this);
    var $psel = $('#'+parentID);
    if (!$psel.is(':input')) {
        // pointed at wrapper, fix
        $psel = $psel.find(':input');
    }
    $csel.multipleSelect({filter:true});
    $csel.parent().find('div.ui-multiselect').hide();
    // use map as a status data store
    map.stored_pas = { };
    var orig_sel = { };
    $csel.find(':selected').each(function() { orig_sel[$(this).val()] = true; });
    $csel.find('option').remove();
    var f =
        function() {
            var $empty = $('span#'+$csel.attr('id')+'-disabled');
            // this should work with both multi-select and single select parents
            // and even with hidden elements!!!!
            var sel = [];
            if ($psel.is("select")) {
                $psel.find(':selected').each(function() { sel.push($(this).val()); });
            }
            else {
                // assume hidden
                if (!!$psel.val()) {
                    $psel.val().split(/,\s*/).each(function(ord,val) { sel.push(val); });
                }
            }
            if (!sel.length) {
                $empty.show();
                $csel.find('option').remove();
                $csel.parent().find('div.ui-multiselect').hide();
                // reset map.stored_pas
                map.stored_pas = { };
            }
            else {
                $empty.hide();
                var newPAs = [];
                var remPAs = [];
                for (var n = 0; n < sel.length; n++) {
                    if (!map.stored_pas[sel[n]]) {
                        newPAs.push(sel[n]);
                    }
                }
                var sel_hash = {};
                for (var y = 0; y < sel.length; y++) {
                    sel_hash[sel[y]] = true;
                }
                for (var id in map.stored_pas) {
                    if (!sel_hash[id]) {
                        remPAs.push(id);
                    }
                }
                // make sure removed removed
                for (var i = 0; i < remPAs.length; i++) {
                    $csel.find('option.pa-'+remPAs[i]).remove();
                }
                // make sure added added
                for (var i = 0; i < newPAs.length; i++) {
                    var paID = newPAs[i];
                    var children = map.children[paID] || [];
                    for (var j = 0; j < children.length; j++) {
                        var ch = children[j];
                        $csel.append($('<OPTION/>').addClass('pa-'+paID).val(ch.id)
                            .text(ch.name).prop('selected',orig_sel[ch.id]));
                    }
                }
                $csel.multipleSelect('refresh');
                // reset map.stored_pas
                map.stored_pas = { };
                orig_sel = { };
                for (var i = 0; i < sel.length; i++) {
                    map.stored_pas[sel[i]] = true;
                }
            }
        };
    $psel.on('change',f);
    f();
};

$.fn.dependentSingleselect = function(parentID,map) {
    var $csel = $(this);
    var $psel = $('#'+parentID);
    // use map as a status data store
    map.stored_pas = { };
    var orig_sel = { };
    orig_sel[$csel.val()] = true;
    $csel.find('option').remove();
    $csel.append($('<OPTION />').val(0).text('--Select Below--'));
    var f =
        function() { 
            var $empty = $('span#'+$csel.attr('id')+'-disabled');
            // this should work with both multi-select and single select parents
            // and even with hidden elements!!!!
            var sel = [];
            if ($psel.is("select")) {
                $psel.find(':selected').each(function() { sel.push($(this).val()); });
            }
            else {
                // assume hidden
                if (!!$psel.val()) {
                    sel = $psel.val().split(',');
                }
            }
            if (!sel.length) {
                $empty.show();
                $csel.find('option').remove();
                $csel.hide();
                // reset map.stored_pas
                map.stored_pas = { };
            }
            else {
                $empty.hide();
                var newPAs = [];
                var remPAs = [];
                for (var n = 0; n < sel.length; n++) {
                    if (!map.stored_pas[sel[n]]) {
                        newPAs.push(sel[n]);
                    }
                }
                var sel_hash = {};
                for (var y = 0; y < sel.length; y++) {
                    sel_hash[sel[y]] = true;
                }
                for (var id in map.stored_pas) {
                    if (!sel_hash[id]) {
                        remPAs.push(id);
                    }
                }
                // make sure removed removed
                for (var i = 0; i < remPAs.length; i++) {
                    $csel.find('option.pa-'+remPAs[i]).remove();
                }
                // make sure added added
                for (var i = 0; i < newPAs.length; i++) {
                    var paID = newPAs[i];
                    var children = map.children[paID] || [];
                    for (var j = 0; j < children.length; j++) {
                        var ch = children[j];
                        $csel.append($('<OPTION/>').addClass('pa-'+paID).val(ch.id)
                            .text(ch.name.truncate(35)).prop('selected',orig_sel[ch.id]));
                    }
                }
                $csel.show();
                // reset map.stored_pas
                map.stored_pas = { };
                orig_sel = { };
                for (var i = 0; i < sel.length; i++) {
                    map.stored_pas[sel[i]] = true;
                }
            }
        };
    $psel.on('change',f);
    f();
};

// Extended tooltip with desired behavior
$.fn.pxTooltip = function() {
    $(this).tooltip({ 
        content: function () { return $(this).prop('title'); },
        open: function(event,ui) {
            if (typeof(event.originalEvent) === 'undefined') {
                return false;
            }
            var $id = $(ui.tooltip).attr('id');
            // close any lingering tooltips
            $('div.ui-tooltip').not('#' + $id).remove();
        }
    }).off("focusin focusout");
};

// Extended dialog with min/max icons
$.fn.pxDialog = function(config) {
    var $src = $(this);
    var $dialog = $src.dialog(config);
    if (config.extended) {
        var extendedConfig = !!config.extendedConfig ? 
            {
                maximizable:true,
                closable:true,
                minimizable:true,
                //collapsable:true,
                dblclick:'maximize',
                minimizeLocation:'left',
                "icons": { "close": "ui-icon-close" }
            } : config.extendedConfig;
        return $dialog.dialogExtend(extendedConfig);
    }
    else {
        return $dialog;
    }
};

$.fn.centerMe = function() {
    var $ref = $(this);
    $ref.css("position","absolute");
    $ref.css("top", Math.max(0, (($(window).height() - $ref.outerHeight()) / 2) + 
        $(window).scrollTop()) + "px");
    $ref.css("left", Math.max(0, (($(window).width() - $ref.outerWidth()) / 2) + 
        $(window).scrollLeft()) + "px");
    return $ref;
};

$.fn.prepareAddresses = function() { 
    var $w = $(this);
    $w.find('.address-container:not(.examined)').each(
        function() {
            var $c = $(this);
            $c.find('input[type="text"],textarea,select')
                .on('change',function() { $(this).addClass('dirty').closest('.address-container').removeClass('geocoded').addClass('examined'); });
        }
    );
};

$.fn.closeContainingLayer = function() {
    $(this).closest('.dialog-source').dialog('close');
};

$.fn.submitAddresses = function(onsuccess,TID) {
    var $f = $(this);
    var $a = $f.find('.address-container:not(.geocoded,.is-template):first');
    if (!$a.length) {
        if (TID) clearTimeout(TID);
        hideTimer();
        $a.find('.template-geocoded').removeClass('template-geocoded').removeClass('geocoded');
        onsuccess();
        return;
    }
    if (!$a.find('input:first').val() || !$a.find('input:first').val().trim()) {  
        // SET GEO-CODE NULL
        $a.find('input.latitude,input.longitude,input.county').val('');
        $a.addClass('geocoded');
        // proceed
        $f.submitAddresses(onsuccess,TID);
    }
    else if (!$a.find('.dirty').length && !!$a.find('input.latitude').val() && !!$a.find('input.longitude').val() && !!$a.find('input.county').val()) { 
        $a.addClass('geocoded');
        // proceed
        $f.submitAddresses(onsuccess,TID);
    }
    else {
        if (!$('.timer').is(':visible')) {
            TID = setTimeout(function() { showAndPositionTimer('&nbsp;&nbsp;Requesting Map Data'); }, 300);
        }
        var geocoder = new google.maps.Geocoder();
        var aparts = [];
        $a.find('input[type="text"],textarea,select').each(function() { aparts.push($(this).is('select')?$(this).find(':selected').text():$(this).val()); });
        geocoder.geocode({'address':aparts.join(' ')},
            function(results,status) { 
                if (status !== google.maps.GeocoderStatus.OK || results.length === 0) {
                    if (TID) clearTimeout(TID);
                    hideTimer();
                    textDialog('Your address [' + aparts.join(' ') + '] could not parsed by Google Maps.  It may not be valid.','Address Error',{width:'500px'});
                    return;
                }
                /*
                else if (results.length !== 1) {
                    for (var i = 0, j = results.length; i < j; i++) {
                        alert(results[i].geometry.location.lat() + " : " + results[i].geometry.location.lng());
                    }
                    if (TID) clearTimeout(TID);
                    hideTimer();
                    textDialog('Your address [' + aparts.join(' ') + '] could not parsed by Google Maps.  More than one match was found for this address.','Address Error',{width:'500px'});
                    return;
                }
                */
                else {
                    var latlng = results[0].geometry.location;
                    // we rely on lat being before lng...
                    $a.find('input.latitude').val(latlng.lat());
                    $a.find('input.longitude').val(latlng.lng());
                    // find county
                    var st, cnty, city, zip;
                    for (var i = 0; i < results[0].address_components.length; i++) {
                        var part = results[0].address_components[i];
                        for (var j = 0; j < part.types.length; j++) {
                            if (part.types[j] === 'administrative_area_level_2') {
                                cnty = part.long_name;
                            }
                            else if (part.types[j] === 'administrative_area_level_1') {
                                st = part.short_name;
                            }
                            else if (part.types[j] === 'locality') {
                                city = part.short_name;
                            }
                            else if (part.types[j] === 'postal_code') {
                                zip = part.short_name;
                            }
                        }
                    }
                    var $inputs = $a.find('input[type="text"],textarea,select');
                    if (st !== $($inputs.get($inputs.length-2)).find('option:selected').text() || zip !== $($inputs.get($inputs.length-1)).val()) {
                        if (TID) clearTimeout(TID);
                        hideTimer();
                        textDialog('Your address [' + aparts.join(' ') + '] could not parsed.  It may not be valid.','Address Error',{width:'500px'});
                        return;
                    }
                    if (!cnty) cnty = city;
                    $a.find('input.county').val(st+'-'+cnty);
                    $a.addClass('geocoded');
                    if (TID) clearTimeout(TID);
                    hideTimer();
                    $f.submitAddresses(onsuccess,TID);
                }
            }
        );
    }
};

jQuery.cachedScript = function( url, options ) {
    // Allow user to set any option except for dataType, cache, and url
    options = $.extend( options || {}, {
        dataType: "script",
        cache: true,
        url: url,
        error: function(data) {
            adminError(data.responseText,data.exception===true?"Intercepted Exception" : "Unintercepted Exception");
        }
    });

    // Use $.ajax() since it is more flexible than $.getScript
    // Return the jqXHR object so we can chain callbacks
    return jQuery.ajax( options );
};

// CONFIGURE HEADERS - REQUIRED BY SPRING CSRF PROTECTION
$.ajaxSetup({
    cache: false,
    beforeSend: function(xhr) {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        if ($("meta[name='_csrf']").length) {
            xhr.setRequestHeader(header, token);
        }
        else {
            debug('No token found when rewriting request header')
        }
    }
});

$.fn.inlineStyler = function(options) {
    var settings = $.extend({
        // These are the defaults.
        // taken from http://templates.mailchimp.com/resources/email-client-css-support
        'propertyGroups': {
            '*': ['border', 'border-radius', 'box-shadow', 'height', 'margin', 'padding', 'width', 'max-width', 'min-width', 'border-collapse', 'border-spacing', 'caption-side', 'empty-cells', 'table-layout', 'direction', 'font', 'font-family', 'font-style', 'font-variant', 'font-size', 'font-weight', 'letter-spacing', 'line-height', 'text-align', 'text-decoration', 'text-indent', 'text-overflow', 'text-shadow', 'text-transform', 'white-space', 'word-spacing', 'word-wrap', 'vertical-align', 'color', 'background', 'background-color', 'background-image', 'background-position', 'background-repeat', 'Opacity', 'bottom', 'clear', 'clip', 'cursor', 'display', 'float', 'left', 'opacity', 'outline ', 'overflow', 'position', 'resize ', 'right', 'top', 'visibility', 'z-index', 'list-style-image', 'list-style-position', 'list-style-type'],
            'block': ['margin', 'padding']
        },
        // taken from http://www.w3schools.com/tags/default.asp
        'elementGroups': {
            '*': ['A', 'ABBR', 'ACRONYM', 'ADDRESS', 'APPLET', 'AREA', 'ARTICLE', 'ASIDE', 'AUDIO', 'B', 'BASE', 'BASEFONT', 'BDI', 'BDO', 'BIG', 'BLOCKQUOTE', 'BODY', 'BR', 'BUTTON', 'CANVAS', 'CAPTION', 'CENTER', 'CITE', 'CODE', 'COL', 'COLGROUP', 'COMMAND', 'DATALIST', 'DD', 'DEL', 'DETAILS', 'DFN', 'DIALOG', 'DIR', 'DIV', 'DL', 'DT', 'EM', 'EMBED', 'FIELDSET', 'FIGCAPTION', 'FIGURE', 'FONT', 'FOOTER', 'FORM', 'FRAME', 'FRAMESET', 'H1', 'HEAD', 'HEADER', 'HR', 'HTML', 'I', 'IFRAME', 'IMG', 'INPUT', 'INS', 'KBD', 'KEYGEN', 'LABEL', 'LEGEND', 'LI', 'LINK', 'MAP', 'MARK', 'MENU', 'META', 'METER', 'NAV', 'NOFRAMES', 'NOSCRIPT', 'OBJECT', 'OL', 'OPTGROUP', 'OPTION', 'OUTPUT', 'P', 'PARAM', 'PRE', 'PROGRESS', 'Q', 'RP', 'RT', 'RUBY', 'S', 'SAMP', 'SCRIPT', 'SECTION', 'SELECT', 'SMALL', 'SOURCE', 'SPAN', 'STRIKE', 'STRONG', 'STYLE', 'SUB', 'SUMMARY', 'SUP', 'TABLE', 'TBODY', 'TD', 'TEXTAREA', 'TFOOT', 'TH', 'THEAD', 'TIME', 'TITLE', 'TR', 'TRACK', 'TT', 'U', 'UL', 'VAR', 'VIDEO', 'WBR']
        }
    }, options);
    this.each(function(index, value) {
        for (var group in settings.elementGroups) {
            for (var i = 0, len = settings.elementGroups[group].length; i < len; i++) {
                // search for all these tags
                var elements = $(this).find(settings.elementGroups[group][i]);
                elements.each(function() {
                // get the current style rules
                    var $element = $(this);
                    var ruleProperties = [];
                    if ($element.attr('style')) {
                        var styles = $(this).attr('style');
                        ruleProperties = styles.split(';');
                    } 
                    else {
                        ruleProperties = [];
                    }
                    var propHash = {};
                    for (var n = 0; n < ruleProperties.length; n++) { var thing = ruleProperties[n].split(":"); if (thing.length > 1) { propHash[thing[0].trim()] = thing[1].trim(); } } 
                    // find all the properties in this rule group
                    for (var i = 0, len = settings.propertyGroups[group].length; i < len; i++) {
                        var thisProp = settings.propertyGroups[group][i];
                        var val = $element.css(thisProp);
                        if (thisProp === 'height') {
                            if ($element.hasClass('preview-form-body')) {
                                continue;
                            }
                            var h = $element.height();
                            if (!!h && h !== 0) {
                                propHash[thisProp] = h;
                            }
                        }
                        else if (thisProp === 'width') {
                            var w = $element.height();
                            if (!!w && w !== 0) {
                                propHash[thisProp] = w;
                            }
                        }
                        else if (!!val) {
                            if ((thisProp === 'min-width' || thisProp === 'min-height') && val === '0px') continue;
                            propHash[thisProp] = $element.css(thisProp);
                        }
                    }
                    $element.css(propHash);
                });
            }
        }
    });
};

(function(g){function k(c){c&&c.printPage?c.printPage():setTimeout(function(){k(c)},50)}function l(c){c=a(c);a(":checked",c).each(function(){this.setAttribute("checked","checked")});a("input[type='text']",c).each(function(){this.setAttribute("value",a(this).val())});a("select",c).each(function(){var b=a(this);a("option",b).each(function(){b.val()==a(this).val()&&this.setAttribute("selected","selected")})});a("textarea",c).each(function(){var b=a(this).attr("value");if(a.browser.b&&this.firstChild)this.firstChild.textContent=
b;else this.innerHTML=b});return a("<div></div>").append(c.clone()).html()}function m(c,b){var i=a(c);c=l(c);var d=[];d.push("<html><head><title>"+b.pageTitle+"</title>");if(b.overrideElementCSS){if(b.overrideElementCSS.length>0)for(var f=0;f<b.overrideElementCSS.length;f++){var e=b.overrideElementCSS[f];typeof e=="string"?d.push('<link type="text/css" rel="stylesheet" href="'+e+'" >'):d.push('<link type="text/css" rel="stylesheet" href="'+e.href+'" media="'+e.media+'" >')}}else a("link",j).filter(function(){return a(this).attr("rel").toLowerCase()==
"stylesheet"}).each(function(){d.push('<link type="text/css" rel="stylesheet" href="'+a(this).attr("href")+'" media="'+a(this).attr("media")+'" >')});d.push('<base href="'+(g.location.protocol+"//"+g.location.hostname+(g.location.port?":"+g.location.port:"")+g.location.pathname)+'" />');d.push('</head><body style="'+b.printBodyOptions.styleToAdd+'" class="'+b.printBodyOptions.classNameToAdd+'">');d.push('<div class="'+i.attr("class")+'">'+c+"</div>");d.push('<script type="text/javascript">function printPage(){focus();print();'+
(!a.browser.opera&&!b.leaveOpen&&b.printMode.toLowerCase()=="popup"?"close();":"")+"}<\/script>");d.push("</body></html>");return d.join("")}var j=g.document,a=g.jQuery;a.fn.printElement=function(c){var b=a.extend({},a.fn.printElement.defaults,c);if(b.printMode=="iframe")if(a.browser.opera||/chrome/.test(navigator.userAgent.toLowerCase()))b.printMode="popup";a("[id^='printElement_']").remove();return this.each(function(){var i=a.a?a.extend({},b,a(this).data()):b,d=a(this);d=m(d,i);var f=null,e=null;
if(i.printMode.toLowerCase()=="popup"){f=g.open("about:blank","printElementWindow","width=650,height=440,scrollbars=yes");e=f.document}else{f="printElement_"+Math.round(Math.random()*99999).toString();var h=j.createElement("IFRAME");a(h).attr({style:i.iframeElementOptions.styleToAdd,id:f,className:i.iframeElementOptions.classNameToAdd,frameBorder:0,scrolling:"no",src:"about:blank"});j.body.appendChild(h);e=h.contentWindow||h.contentDocument;if(e.document)e=e.document;h=j.frames?j.frames[f]:j.getElementById(f);
f=h.contentWindow||h}focus();e.open();e.write(d);e.close();k(f)})};a.fn.printElement.defaults={printMode:"iframe",pageTitle:"",overrideElementCSS:null,printBodyOptions:{styleToAdd:"padding:10px;margin:10px;",classNameToAdd:""},leaveOpen:false,iframeElementOptions:{styleToAdd:"border:none;position:absolute;width:0px;height:0px;bottom:0px;left:0px;",classNameToAdd:""}};a.fn.printElement.cssElement={href:"",media:""}})(window);