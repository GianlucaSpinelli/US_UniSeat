<%@ page import="control.utili.SessionManager" %>
<%@ page import="model.pojo.TipoUtente" %><%--
  Created by IntelliJ IDEA.
  User: marco
  Date: 10/01/2020
  Time: 12:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    Utente u = SessionManager.getUtente(session);
    String errorMessage = null;
    errorMessage = SessionManager.getError(session);
    if (errorMessage != null) {
        SessionManager.cleanError(session);
    }
    if (u == null) {
        response.sendRedirect(request.getContextPath() + "/_comuni/login.jsp");
    } else if (!u.getTipoUtente().equals(TipoUtente.ADMIN)) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
%>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>UniSeat</title>
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <meta name="keywords">
    <meta name="description">

    <!-- Favicons -->
    <link href="${pageContext.request.contextPath}/img/favicon.png" rel="icon">
    <link href="${pageContext.request.contextPath}/img/apple-touch-icon.png" rel="apple-touch-icon">

    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,300i,400,400i,700,700i|Poppins:300,400,500,700"
          rel="stylesheet">

    <!-- Bootstrap CSS File -->
    <link href="${pageContext.request.contextPath}/lib/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Libraries CSS Files -->
    <link href="${pageContext.request.contextPath}/lib/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/lib/animate/animate.min.css" rel="stylesheet">

    <!-- Main Stylesheet File -->
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>

<body>

<!--==========================
Header
============================-->
<%@ include file="/_fragments/header.jsp" %>

<main>
    <section id="hero">
        <div class="hero-container">
            <div class="container  wow fadeInUp">
                <br>
                <div id="contact">
                    <h3 class="section-title">Registra docente</h3>
                    <div class="container wow fadeInUp mt-5">
                        <div class="row justify-content-center">
                            <div class="col-lg-4 col-md-4">
                                <div class="form">
                                    <div id="exercitationrormessage"></div>
                                    <form action="/iscrizioneDocente" method="post"
                                          class="contactForm" onsubmit="if(validate()==false){return false;}">
                                        <% if (errorMessage != null) { %>
                                        <div class="alert alert-danger" role="alert">
                                            <%=errorMessage%>
                                        </div>
                                        <% }
                                            if (SessionManager.getMessage(session) != null) { %>
                                        <div class="alert alert-success" role="alert">
                                            <%=SessionManager.getMessage(session)%>
                                        </div>
                                        <%}%>
                                        <div class="form-group">
                                            <input type="text" name="name" class="form-control" id="nome"
                                                   placeholder="Nome">
                                            <div class="validation" id="errnome"></div>
                                        </div>
                                        <div class="form-group">
                                            <input type="text" class="form-control" name="name" id="cognome"
                                                   placeholder="Cognome">
                                            <div class="validation" id="errcognome"></div>
                                        </div>
                                        <div class="form-group">
                                            <input type="text" class="form-control" name="email" id="email"
                                                   placeholder="Email">
                                            <div class="validation" id="erremail"></div>
                                        </div>
                                        <div class="form-group">
                                            <input type="password" class="form-control" name="password" id="psw"
                                                   rows="5" placeholder="Password">
                                            <div class="validation" id="errpass"></div>
                                        </div>
                                        <div class="form-group">
                                            <input type="password" class=" form-control" name="password" id="Cpsw"
                                                   rows="5" placeholder="Verifica Password">
                                            <div class="validation" id="errCpass"></div>
                                        </div>
                                        <div class="form form-group ">
                                            <button type="submit">Registra</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </section>

</main>
<%@ include file="../_fragments/contattaci.jsp" %>

<%@ include file="../_fragments/footer.jsp" %>
<!-- JavaScript Libraries -->
<script src="${pageContext.request.contextPath}/lib/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/lib/jquery/jquery-migrate.min.js"></script>
<script src="${pageContext.request.contextPath}/lib/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/lib/easing/easing.min.js"></script>
<script src="${pageContext.request.contextPath}/lib/wow/wow.min.js"></script>
<script src="${pageContext.request.contextPath}/lib/waypoints/waypoints.min.js"></script>
<script src="${pageContext.request.contextPath}/lib/counterup/counterup.min.js"></script>
<script src="${pageContext.request.contextPath}/lib/superfish/hoverIntent.js"></script>
<script src="${pageContext.request.contextPath}/lib/superfish/superfish.min.js"></script>
<!-- Contact Form JavaScript File -->
<%--<script src="${pageContext.request.contextPath}/contactform/contactform.js"></script>--%>
<script src="${pageContext.request.contextPath}/js/validate.js"></script>
<!-- Template Main Javascript File -->
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
