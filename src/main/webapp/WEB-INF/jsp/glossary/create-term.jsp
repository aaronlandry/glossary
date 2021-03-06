<%-- 
    Document   : create-term
    Created on : Jun 20, 2015, 10:09:54 PM
    Author     : Aaron
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<form:form autocomplete="off" commandName="entity" class="entity-form" method="post" action="/Superiad/CRUD/DoCreate/Term.do"
        enctype="multipart/form-data" onsubmit="return false;">
    <table class="form">
        <tr>
            <td>
                Term<span class="required">*</span>:
            </td>
            <td class="validatable">
                <form:input path="name" />
            </td>
        </tr>
        <tr>
            <td>
                Description<span class="required">*</span>:
            </td>
            <td class="validatable">
                <form:textarea rows="10" path="definition" />
            </td>
        </tr>
        <tr>
            <td>
                Match Lowercase<span class="required">*</span>:
            </td>
            <td class="validatable">
                <form:checkbox path="matchLowercase" />
            </td>
        </tr>
        <tr>
            <td>
                Categories<span class="required">*</span>:
            </td>
            <td class="validatable category-container">
                <div class='category-select-div'>
                    <form:select id="categories-0" multiple="true" path="categories">
                        <c:forEach var="category" items="${entity.categories}">
                            <OPTION SELECTED value="${category.id}">${category.name}</OPTION>
                        </c:forEach>
                    </form:select>
                </div> 
                <div id="category-selector-0" class="category-selector">
                    Loading ...
                    <SCRIPT type="text/javascript">
                        $(function() { glossary.initializeCategorySelector($('#category-selector-0'),${categories});  });
                    </SCRIPT>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                Novels:
            </td>
            <td class="validatable">
                <form:select id="novels-0" multiple="true" path="novels">
                    <form:options items="${novels}" itemValue="id" itemLabel="name" />
                </form:select>
                <SCRIPT type="text/javascript">$("#novels-0").multipleSelect({filter:true});</SCRIPT>
            </td>
        </tr>
        <tr>
            <td>
                Dated:
            </td>
            <td class="validatable">
                <input type="checkbox" <c:if test="${not empty entity.eventDate && not empty entity.eventDate.epoch}">CHECKED</c:if> onclick=" if ($(this).is(':checked')) { $(this).parent().find('div').show(); } else { $(this).parent().find('div').hide();$(this).parent().find('div').find(':input[type=text]').val('');$(this).parent().find('div').find('select').val(0); } " />
                <div style="display:${not empty entity.eventDate && not empty entity.eventDate.epoch ? 'block' : 'none'};">
                    Year&nbsp; <form:input path="eventDate.relativeDate" cssStyle="width:75px" />
                    &nbsp;of&nbsp; 
                    <form:select path="eventDate.epoch" cssStyle="width:325px">
                        <form:option value="0" label="--Select Below--" />
                        <form:options items="${epochs}" itemValue="value" itemLabel="htmlLabel" />
                    </form:select>
                </div>
            </td>
        </tr>
    </table>
</form:form>