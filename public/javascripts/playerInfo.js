new Vue({
  el: "#playerInfoForm",
  data: {
    savePlayerSuccess: false,
    savePlayerSuccessMsg: '',
    savePlayerFailure: false,
    savePlayerFailureMsg: ''
  },
  methods: {
    savePlayerInfo: function (event) {
      event.preventDefault();
      var $form = $('#playerInfoForm');

      this.$http.headers.common['X-CSRF-TOKEN'] = document.querySelector('[name="csrfToken"]').getAttribute('value');
      this.$http.post($form.attr('action'), $form.serialize(), {headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
        .then(function(success) {
          this.savePlayerSuccess = true;
          this.savePlayerSuccessMsg = success.data['message'];
          this.savePlayerFailure = false;
        }, function(failure) {
          this.savePlayerFailure = true;
          this.savePlayerFailureMsg = failure.data['message'];
          this.savePlayerSuccess = false;
        });
    }
  }
});
