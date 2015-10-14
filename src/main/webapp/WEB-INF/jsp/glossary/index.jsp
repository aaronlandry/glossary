<%-- 
    Document   : index
    Created on : Jun 19, 2015, 11:54:51 AM
    Author     : Aaron
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="X-UA-Compatible" content="IE=11; IE=10; IE=9; IE=8; IE=7; IE=EDGE" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="PRAGMA" content="NO-CACHE" />
        <meta name="CACHE-CONTROL" content="NO-STORE" />
        <meta name="COPYRIGHT" content="&amp;copy; 2007-2019 Superiad" />
        <meta name="EXPIRES" content="Mon, 25 Aug 1977 11:12:01 GMT" />
        <meta name="ROBOTS" content="NONE" />
        <meta name="GOOGLEBOT" content="NOARCHIVE" />
        <%--
        <meta name="_csrf" content="${_csrf.token}"/>
        <!-- default header name is X-CSRF-TOKEN -->
        <meta name="_csrf_header" content="${_csrf.headerName}"/>
        --%>
        
        <script type="text/javascript" src="/Superiad/javascript/lib/jquery.js"></script>
        <script type="text/javascript" src="/Superiad/javascript/lib/jquery-ui.js"></script>
        <script type="text/javascript" src="/Superiad/javascript/lib/underscore.js"></script>
        <script type="text/javascript" src="/Superiad/javascript/lib/backbone.js"></script>
        <script type="text/javascript" src="/Superiad/javascript/lib/backbone.marionette.js"></script>
        <script type="text/javascript" src="/Superiad/javascript/lib/backbone.babysitter.js"></script>
        <script type="text/javascript" src="/Superiad/javascript/lib/jquery-dialogextend.js"></script>
        <script type="text/javascript" src="/Superiad/javascript/lib/multiple-select.js"></script>
        <script type="text/javascript" src="/Superiad/javascript/lib/json2.js"></script>
        <script type="text/javascript" src="/Superiad/javascript/lib/treetable.js"></script>
        <script type="text/javascript" src="/Superiad/javascript/lib/customSelect.js"></script>
        <script type="text/javascript" src="/Superiad/javascript/jquery-ext.js"></script>
        <script type="text/javascript" src="/Superiad/javascript/login.js"></script>
        <script type="text/javascript" src="/Superiad/javascript/util.js"></script>
        <script type="text/javascript" src="/Superiad/javascript/glossary.js"></script>
        <link rel="stylesheet" type="text/css" href="/Superiad/styles/main.css"/>
        <link rel="stylesheet" type="text/css" href="/Superiad/styles/lib/jquery.css"/>
        <link rel="stylesheet" type="text/css" href="/Superiad/styles/glossary.css"/>
        <link rel="stylesheet" type="text/css" href="/Superiad/styles/lib/icomoon.css"/>
        <link rel="stylesheet" type="text/css" href="/Superiad/styles/lib/multiple-select.css"/>
        <link rel="stylesheet" type="text/css" href="/Superiad/styles/lib/treetable.css"/>
        <link rel="stylesheet" type="text/css" href="/Superiad/styles/lib/customSelect.css"/>
        
        <%--
        <script type="text/javascript" src="/Superiad/javascript/compressed-min.js"></script>
        <link rel="stylesheet" type="text/css" href="/Superiad/styles/compressed-min.css"/>
        --%>
        
        <!--[if lt IE 9]>
            <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->
        <title>Superiad Glossary</title>
    </head>
    <body id="glossary">
        <table id="structure">
            <tr id="topNavElement">
                <td colspan="2" id="innerTopNavElement">
                    UPPER NAV
                </td>
            </tr>
            <tr>
                <td id="leftNavElement">
                    <%-- DIV REQUIRED FOR SCROLLING --%>
                    <div>
                        LEFT NAV
                    </div>
                </td>
                <td id="mainElement">
                    <%-- DIV REQUIRED FOR SCROLLING --%>
                    <div>
                        CONTENT
                    </div>
                </td>
            </tr>
        </table>
        
        <%-- TEMPLATES --%>
        <script type="text/template" id="terms-template">
            <tbody>
            
                
            </tbody>
            <caption class="terms-count"></caption>
        </script>
        <script type="text/template" id="term-template">
            <td>
                <table class="term-table">
                    <tr>
                        <td class="term">
                            <\%= name %>
                            <span style="font-size:.9em;" class="update-term icon icon-gold icon-update" title="Update Term"></span> 
                            <span style="font-size:.9em;" class="delete-term icon icon-gold icon-delete" title="Delete Term"></span>
                        </td>
                        <td class="categories">
                            <b>Categories</b>: <\% _.each(categories, function(cat,ind) { %> <\% if(ind>0) print(' : '); %> <\%= cat.longName %>   <\% }); %>
                        </td>
                    </tr>
                    <tr class="epochs">
                        <td colspan="2">
                            <b>Epoch</b>: <\% if(eventDate && eventDate.epoch) print(eventDate.epoch.name); %>
                                <\% if(!eventDate || !eventDate.epoch) print('None'); %>
                        </td>
                    </tr>
                    <tr class="novels">
                        <td colspan="2">
                            <b>Novels</b>: <\% if(novels.length===0) print('None'); %><\% _.each(novels, function(novel,ind) { %> <\% if(ind>0) print(' : '); %> <\%= novel.name %>   <\% }); %>
                        </td>
                    </tr>
                    <tr>
                        <td class="description" colspan="2">
                            <\%= definition %>
                        </td>
                    </tr>
                </table>
            </td>
        </script>
        <script type="text/template" id="letters-template">
        </script>
        <script type="text/template" id="letter-template">
            <a <\% if(selected) print("class='selected'"); %> ><\%= value %></a>
        </script>
        <script type="text/template" id="categories-template">
        </script>
        <script type="text/template" id="category-template">
            <li class="indent-<\%= indent %>"><a <\% if(selected) print("class='selected'"); %> ><\%= name %></a></li>
        </script>
        <script type="text/template" id="novels-template">
        </script>
        <script type="text/template" id="novel-template">
            <a <\% if(selected) print("class='selected'"); %> ><\%= name %></a>
        </script>
        <script type="text/template" id="epochs-template">
        </script>
        <script type="text/template" id="epoch-template">
            <a <\% if(selected) print("class='selected'"); %> ><\%= name %> (<\%= startPoint %> - <\% if(endPoint == -1) print('?'); %> <\% if(endPoint != -1) print(endPoint); %>)</a>
        </script>
        <script type="text/template" id="nav-template">
            <div id="nav-modes">
                <button id="mode-letter-button" class="link-button-green link-button">
                    BY LETTER
                </button>
                <button id="mode-category-button" class="link-button-dkgray link-button">
                    BY CATEGORY
                </button>
                <button id="mode-epoch-button" class="link-button-dkgray link-button">
                    BY EPOCH
                </button>
                <button id="mode-novel-button" class="link-button-dkgray link-button">
                    BY NOVEL
                </button>
            </div>
            <div id="nav-center">
                Superiad Glossary
            </div>
            <div id="nav-controls">
                SHOWING:&nbsp; 
                <button id="show-category-button" class="link-button-green link-button">
                    CATEGORIES
                </button>
                <button id="show-epoch-button" class="link-button-green link-button">
                    EPOCHS
                </button>
                <button id="show-novel-button" class="link-button-orange link-button">
                    NOVELS
                </button>
                <button id="add-term" class="link-button-primary link-button">
                    ADD
                </button>
                <button id="add-yencari-term" class="link-button-primary link-button">
                    ADD Y
                </button>
                <button id="manage-categories" class="link-button-primary link-button">
                    CATS
                </button>
                <button id="cross-reference" class="link-button-primary link-button">
                    CROSS REFERENCE
                </button>
            </div>
            <div id="nav-clear"></div>
        </script>
    </body>
</html>