<%--
  Created by IntelliJ IDEA.
  User: MyMindWorld
  Date: 08.09.2020
  Time: 23:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Day reporter</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="./vendor/sockjs/sockjs.js"></script>
    <script src="./vendor/stomp/stomp.js"></script>
    <meta <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>>
    <script src="<c:url value="/vendor/jquery/jquery-2.1.3.min.js"/>"></script>
    <script src="<c:url value="/vendor/select2/select2.min.js"/>"></script>
    <script src="<c:url value="/js/PlannerUtils.js"/>"></script>
    <script src="<c:url value="/js/loading-spinner.js"/>"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/select2/select2.min.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/font-awesome-4.7.0/css/font-awesome.min.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/font-awesome-5.12.1/css/all.css"/>">
</head>
<body>
<div style="display: inline-block;">
    <h3>
        <button onclick="window.location.href='<c:url value="/Planner"/>'">
            <i class="fas fa-sign-out-alt"></i>
            Planner
        </button>
    </h3>
</div>
<div style="display: inline-block;">
    <h3>Reporter</h3>
</div>
<br/>
<p>Please insert your plan to the textarea down below and press 'Process plan'</p>
<p>Afterwards your report will appear in area on the right. Feel free to press 'Copy to clipboard'</p>

<div style="display: inline-block;">
    <textarea style="width: 600px; height: 150px" id="dayPlan"></textarea>
</div>
<div style="display: inline-block;">
    <textarea style="width: 600px; height: 150px" id="dayReport" disabled></textarea>
    <button id="copyToClipboard" class="e"
            onclick="
            var copyText = document.getElementById('dayReport').innerHTML
            copyToClipboard(copyText)
                    ">
        Copy to clipboard
    </button>
</div>

<br/>
<button id="processPlan" class="e"
        onclick="
            var plan = document.getElementById('dayPlan').value;
            processPlan(plan)
                    ">
    Process plan
</button>
<script>

    function processPlan(Plan) {
        Spinner();
        Spinner.show();

        $.ajax({
            url: '<c:url value="/Planner/processDayPlan"/>',
            type: "POST",
            data: JSON.stringify(Plan),
            contentType: "application/json; charset=utf-8;",
            dataType: "",
            processData: false,
            statusCode: {
                200: function (jqXHR, response) {
                    document.getElementById('dayReport').innerHTML = jqXHR.responseText
                    Spinner.hide();
                }
            },
            error: function (jqXHR, status, errorThrown) {
                // AAAAAAAAAAAA
                if (status == "parsererror") {
                    document.getElementById('dayReport').innerHTML = jqXHR.responseText;
                    Spinner.hide();
                } else {
                    alert('Error! Contact admin!');
                    console.log(jqXHR.responseText);
                    console.log(status);
                    console.log(errorThrown);
                }

                Spinner.hide();
            }
        })


    };
</script>

<script>
    var observe;
    if (window.attachEvent) {
        observe = function (element, event, handler) {
            element.attachEvent('on' + event, handler);
        };
    } else {
        observe = function (element, event, handler) {
            element.addEventListener(event, handler, false);
        };
    }

    function init() {
        var text = document.getElementById('dayPlan');

        function resize() {
            text.style.height = 'auto';
            text.style.height = text.scrollHeight + 'px';
        }

        /* 0-timeout to get the already changed text */
        function delayedResize() {
            window.setTimeout(resize, 0);
        }

        observe(text, 'change', resize);
        observe(text, 'cut', delayedResize);
        observe(text, 'paste', delayedResize);
        observe(text, 'drop', delayedResize);
        observe(text, 'keydown', delayedResize);

        text.focus();
        text.select();
        resize();
    }
</script>
</body>
</html>
</body>