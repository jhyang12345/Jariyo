document.addEventListener("DOMContentLoaded", function(evt) {
  $(".error-retry").on("click tap", function(evt) {
    Android.retryConnection();
  });
});
