function login() {
    $.ajax({
        type: "post",
        url: "http://localhost:8888/redis/login",
        data: {"username": "aaa"},
        success: function (data) {
            $("#msg").append(data);
            var br=document.createElement("div");
            br.innerHTML="<br/>";
            $("#msg").append(br);
        }
    });
}