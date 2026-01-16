<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String nome = (String) session.getAttribute("nomeUtente");
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>UStrike - Home Cliente</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/clienteHome.css">
</head>
<body>
    <div class="logo">
        <img src="${pageContext.request.contextPath}/static/images/logo.png" alt="UStrike Logo">
    </div>

    <div class="hamburger" onclick="toggleMenu()">
        <span></span>
        <span></span>
        <span></span>
    </div>

    <nav class="nav-menu" id="navMenu">
        <ul>
            <li><a href="${pageContext.request.contextPath}/cliente/prenotazioni">Le mie prenotazioni</a></li>
            <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
        </ul>
    </nav>

    <div class="container">
        <h2>La sfida si accende!</h2>
        <p style="text-align:center; font-size:1.2rem; margin-bottom:50px;">
            Benvenuto<%= (nome != null ? ", " + nome : "") %>!
        </p>

        <section class="section">
            <div class="section-content">
                <h3>Divertimento al Bowling</h3>
                <p>
                    Vivi l'emozione del bowling nel nostro centro moderno a Napoli! Piste professionali illuminate 
                    da luci LED spettacolari. Sfida i tuoi amici sulle nostre 8 piste professionali.
                </p>
                <a href="${pageContext.request.contextPath}/cliente/crea-prenotazione?tipo=bowling" class="btn-prenota">
                    Crea Prenotazione Bowling
                </a>
            </div>
            <div class="section-image" 
                 style="background-image: url('${pageContext.request.contextPath}/static/images/bowling-equipment-indoors-still-life.jpg');">
            </div>
        </section>

        <section class="section reverse">
            <div class="section-image" 
                 style="background-image: url('${pageContext.request.contextPath}/static/images/kart.jpeg');">
            </div>
            <div class="section-content">
                <h3>Adrenalina coi Go-Kart</h3>
                <p>
                    Curve mozzafiato, rettilinei veloci!
                    Sfreccia sul nostro circuito professionale indoor di 500 metri con kart elettrici 
                    potenti e sicuri! Ideale per sfide competitive ed eventi.
                </p>
                <a href="${pageContext.request.contextPath}/cliente/crea-prenotazione?tipo=kart" class="btn-prenota">
                    Crea Prenotazione Go-Kart
                </a>
            </div>
        </section>

        <section class="section">
            <div class="section-content">
                <h3>Precisione al Biliardo</h3>
                <p>
                    Tra una corsa e uno strike, concediti una pausa di classe. I nostri tavoli professionali 
                    ti aspettano per sfide di pura concentrazione e strategia. Calibra il tiro e imbuca la vittoria!
                </p>
                <a href="${pageContext.request.contextPath}/biliardo.jsp" class="btn-prenota">
                    Per maggiori informazioni
                </a>
            </div>
            <div class="section-image" 
                 style="background-image: url('${pageContext.request.contextPath}/static/images/biliardo.jpg');">
            </div>
        </section>
    </div>

    <script>
        function toggleMenu() {
            document.getElementById('navMenu').classList.toggle('active');
        }
        document.addEventListener('click', function(e) {
            const hamburger = document.querySelector('.hamburger');
            const menu = document.getElementById('navMenu');
            if (!hamburger.contains(e.target) && !menu.contains(e.target)) {
                menu.classList.remove('active');
            }
        });
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                document.getElementById('navMenu').classList.remove('active');
            }
        });
    </script>
</body>
</html>