<%-- 
    Document   : read-term
    Created on : Aug 5, 2015, 8:38:20 PM
    Author     : Aaron
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<table class="form" style="width:750px;">
        <tr>
            <td>
                Term:
            </td>
            <td class="validatable">
                ${entity.name}
            </td>
        </tr>
        <tr>
            <td>
                Description:
            </td>
            <td class="validatable">
                ${empty entity.parsedDefinition ? entity.definition : entity.parsedDefinition}
            </td>
        </tr>
        <tr>
            <td>
                Aliases<span class="required">*</span>:
            </td>
            <td class="validatable">
                <div class='alias'>Superi: ${entity.yencariExtension.superiName}</div>
                <div class='alias'>a'Yencari: ${entity.yencariExtension.aYencariName}</div>
                <div class='alias'>Sargarath: ${entity.yencariExtension.sargarathName}</div>
                <div class='alias'>First Yencour: ${entity.yencariExtension.firstYencourName}</div>
                <div class='alias'>Second Yencour: ${entity.yencariExtension.secondYencourName}></div>
                <div class='alias'>&AElig;gan Empire: ${entity.yencariExtension.firstAegeaName}</div>
                <div class='alias'>Third Yencour: ${entity.yencariExtension.thirdYencourName}></div>
                <div class='alias'>Second &AElig;gan Empire (Birth): ${entity.yencariExtension.secondAegeaBirthName}</div>
                <div class='alias'>Second &AElig;gan Empire (Uni): ${entity.yencariExtension.secondAegeaUniversityName}</div>
                <div class='alias'>Second &AElig;gan Empire (War): ${entity.yencariExtension.secondAegeaGreatWarName}</div>
                <div class='alias'>Modern Name: ${entity.yencariExtension.modernName}</div>
            </td>
        </tr>
        <tr>
            <td>
                Chara<span class="required">*</span>:
            </td>
            <td class="validatable">
                ${entity.yencariExtension.chara}
            </td>
        </tr>
        <tr>
            <td>
                Marg<span class="required">*</span>:
            </td>
            <td class="validatable">
                ${entity.yencariExtension.marg}
            </td>
        </tr>
        <tr>
            <td>
                Categories:
            </td>
            <td class="validatable category-container">
                <c:choose>
                    <c:when test="${empty entity.categories}">
                        None
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="cat" items="${entity.categories}">
                            <div>
                                ${cat.longName}
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <td>
                Novels:
            </td>
            <td class="validatable">
                <c:choose>
                    <c:when test="${empty entity.novels}">
                        None
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="n" items="${entity.novels}">
                            <div>
                                ${n}
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <td>
                Dated:
            </td>
            <td class="validatable">
                <c:choose>
                    <c:when test="${not empty entity.eventDate && not empty entity.eventDate.epoch}">
                        <div style="display:${not empty entity.eventDate ? 'block' : 'none'};">
                            Year&nbsp; ${entity.eventDate.relativeDate}
                            &nbsp;of&nbsp; ${entity.eventDate.epoch.htmlLabel}
                        </div>
                    </c:when>
                    <c:otherwise>
                        No
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </table>
