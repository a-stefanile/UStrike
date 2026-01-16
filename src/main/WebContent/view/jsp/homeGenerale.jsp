<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>UStrike - Benvenuti</title>
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
            <%-- Qui usiamo i mapping delle tue Servlet --%>
            <li><a href="${pageContext.request.contextPath}/login">Accedi</a></li>
            <li><a href="${pageContext.request.contextPath}/register">Registrati</a></li>
        </ul>
    </nav>

    <div class="container">
        <h2>La sfida si accende!</h2>
        <p style="text-align:center; font-size:1.2rem; margin-bottom:50px;">
            Metti KO i tuoi amici in pista o sulla corsia.
        </p>
        <section class="section">
            <div class="section-content">
                <h3>Divertimento Bowling</h3>
                <p>
                    Vivi l'emozione del bowling nel nostro centro moderno a Napoli! Piste professionali illuminate 
                    da luci LED spettacolari. Sfida i tuoi amici sulle nostre 8 piste professionali.
                </p>
                <%-- Rimandiamo al login perché l'utente anonimo non può prenotare --%>
                <a href="${pageContext.request.contextPath}/login" class="btn-prenota">
                    Accedi per Prenotare
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
                <h3>Adrenalina Go-Kart</h3>
                <p>
                    Sfreccia sul nostro circuito professionale indoor di 500 metri con kart elettrici 
                    potenti e sicuri! Ideale per sfide competitive ed eventi.
                </p>
                <a href="${pageContext.request.contextPath}/login" class="btn-prenota">
                    Accedi per Prenotare
                </a>
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