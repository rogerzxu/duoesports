Vue.use(window.vuelidate.default);
var { required, sameAs, minLength } = window.validators;

new Vue({
  el: "#resetPasswordForm",
  data: {
    password: '',
    confirmPassword: '',
    changePasswordSuccess: false,
    changePasswordSuccessMsg: '',
    changePasswordFailure: false,
    changePasswordFailureMsg: ''
  },
  methods: {
    resetPassword: function(event) {
      event.preventDefault();
      var $form = $('#resetPasswordForm');

      this.$http.headers.common['X-CSRF-TOKEN'] = document.querySelector('[name="csrfToken"]').getAttribute('value');
      this.$http.post($form.attr('action'), $form.serialize(), {headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
        .then(function(success) {
          this.changePasswordSuccess = true;
          this.changePasswordSuccessMsg = success.data['message'];
          this.changePasswordFailure = false;
          window.scrollTo(0,0);
        }, function(failure) {
          this.changePasswordFailure = true;
          this.changePasswordFailureMsg = failure.data['message'];
          this.changePasswordSuccess = false;
          window.scrollTo(0,0);
        });
    }
  },
  validations: {
    password: {
      required,
      minLength: minLength(8)
    },
    confirmPassword: {
      required,
      sameAsPassword: sameAs('password')
    }
  }
});
