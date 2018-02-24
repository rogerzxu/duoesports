Vue.use(window.vuelidate.default);
var { required, email} = window.validators;

new Vue({
  el: "#sendResetPasswordForm",
  data: {
    email: '',
    sendResetEmailSuccess: false,
    sendResetEmailFailure: false,
    sendResetEmailMsg: '',
  },
  methods: {
    sendResetPassword: function (event) {
      event.preventDefault();
      var $form = $('#sendResetPasswordForm');

      this.$http.headers.common['X-CSRF-TOKEN'] = document.querySelector('[name="csrfToken"]').getAttribute('value');
      this.$http.post($form.attr('action'), $form.serialize(), {headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
        .then(function(success) {
          this.sendResetEmailSuccess = true;
          this.sendResetEmailMsg = success.data['message'];
          this.sendResetEmailFailure = false;
        }, function(failure) {
          this.sendResetEmailSuccess = false;
          this.sendResetEmailMsg = failure.data['message'];
          this.sendResetEmailFailure = true;
        });
    }
  },
  validations: {
    email: {
      required,
      email
    }
  }
});