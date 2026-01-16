<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>UStrike - Area Biliardo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/biliardo.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css">
</head>
<body class="biliardo-body">

    <div class="logo-biliardo">
        <img src="${pageContext.request.contextPath}/static/images/logo.png" alt="UStrike Logo">
    </div>

    <main class="main-content">
        <div class="biliardo-card">

            <div class="biliardo-text">
                <h1>Area Biliardo</h1>
                <p>
                    Benvenuto nel cuore della precisione firmata UStrike.  
                    Un ambiente elegante e rilassato, pensato per chi ama il biliardo e la competizione senza rinunciare al divertimento.
                </p>
                <p>
                    <span class="highlight">Senza stress:</span> per il biliardo <span class="highlight">non è richiesta alcuna prenotazione online</span>.  
                    Ti basta raggiungerci nell’area Arcade: il nostro staff ti assegnerà subito il primo tavolo disponibile.
                </p>
                <div class="info-box">
                    🎱 <strong>Vuoi metterti alla prova?</strong><br><br>
                    Nell’area biliardo di UStrike organizziamo periodicamente tornei amatoriali e competitivi, ideali sia per chi gioca per passione sia per chi ama la sfida.
                    <br><br>
                    Per maggiori informazioni, rivolgiti al nostro staff oppure seguici sui nostri canali social.
                </div>
            </div>

            <div class="biliardo-img">
                <img src="${pageContext.request.contextPath}/static/images/tavolobiliardo.jpg"
                     alt="I nostri tavoli da biliardo professionali">
            </div>

        </div>
    </main>

    <div class="nav-actions">
        <a href="${homeUrl}" class="home-btn">
            <i class="fas fa-home"></i> Home
        </a>

        <c:if test="${not empty sessionScope.ruolo}">
            <a href="${pageContext.request.contextPath}/logout" class="logout-btn">
                <i class="fas fa-sign-out-alt"></i> Logout
            </a>
        </c:if>
    </div>

</body>
</html>