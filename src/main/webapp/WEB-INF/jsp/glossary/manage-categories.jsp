<%-- 
    Document   : manage-categories
    Created on : Jun 22, 2015, 11:11:46 AM
    Author     : Aaron
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<form:form autocomplete="off" commandName="entity" class="entity-form" method="post" action="/Superiad/CRUD/DoManageCategories.do"
        enctype="multipart/form-data" onsubmit="return false;">
    <div class="vertical-container" style="width:975px;display:inline-block;">
        <ul class='vertical-form' >
            <a href='javascript:void(false)' onclick="addCategory();return false;">
                Add New Category
            </a>
            <table class="clear-left" id="treetable">
                <tr class="tree-row root-row" data-tt-id="0">
                    <td style="cursor:default;white-space:nowrap;;" colspan="4">ALL</td>
                </tr>
                <c:forEach var="category" items="${categories}">
                    <tr class="tree-row" data-tt-id="${category.id}" data-tt-parent-id="${empty category.parent.id ? 0 : category.parent.id}">
                        <td style='white-space:nowrap;'>
                            <input type='hidden' name='category-ids' value='${category.id}' />
                            <input type='text' style='width:125px;' maxlength='50' name='category-name-${category.id}' 
                                   value='<c:out value="${category.name}"/>'>
                            <input type='hidden' class="category-parent" name='category-parent-${category.id}' 
                                   value='${empty category.parent ? 0 : category.parent.id}'>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </ul>
    </div>
</form:form>

<SCRIPT type="text/javascript">
    
function addCategory() {
    var $tbl = $('#treetable');
    var added = $tbl.find('tr.new-row').length;
    var newId = -100 - added;
    var $newRow = $('<tr data-tt-id="' + newId + '" data-tt-parent-id="0"></tr>');
    var $td1 = $("<td style='width:1px;white-space:nowrap;'></td>");
    $td1.append("<input type='hidden' name='category-ids' value='" + newId + "' />");
    $td1.append("<input type='text' style='width:125px;' maxlength='50' name='category-name-" + newId + "' value='' />");
    $td1.append("<input type='hidden' class='category-parent' name='category-parent-" + newId + "' value='0' />");
    $newRow.append($td1);
    $newRow.addClass('new-row').addClass('tree-row');
    var node = $tbl.treetable("node", "0");
    $tbl.treetable("loadBranch", node, $newRow);
    $newRow.find('.indenter').append('--- &nbsp;');
    setDragging();
}

function setDragging() {
    $("#treetable tr.tree-row:not(.root-row)").draggable({
        helper: "clone",
        opacity: .75,
        refreshPositions: true,
        revert: "invalid",
        revertDuration: 300,
        scroll: true
    });
    $("#treetable tr.tree-row").droppable({
        accept: "tr.tree-row",
        drop: function(e, ui) {
            var droppedEl = ui.draggable;
            var pID = droppedEl.data("ttParentId");
            $("#treetable").treetable("move", droppedEl.data("ttId"), $(this).data("ttId"));
            var pID2 = droppedEl.data("ttParentId");
            if (pID === pID2) {
                textDialog('Your move action failed, likely because you tried to move a Category to be a child of itself.  Try moving any children of the Category to new node first.',"Sorry!");
            }
            droppedEl.find('input.category-parent').val($(this).data("ttId"));
        },
        hoverClass: "accept",
        over: function(e, ui) {
            var droppedEl = ui.draggable.parents("tr");
            if(this !== droppedEl[0] && !$(this).is(".expanded")) {
                $("#example-advanced").treetable("expandNode", $(this).data("ttId"));
            }
        }
    });
}

$("#treetable").treetable({ expandable: false, initialState:'expanded' })
    .find('.indenter').append('--- &nbsp;');
setDragging();

</SCRIPT>