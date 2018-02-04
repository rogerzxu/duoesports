Vue.use(window.vuelidate.default);
var { required, sameAs, minLength } = window.validators;

new Vue({
  el: "#changePasswordForm",
  data: {
    currentPassword: '',
    password: '',
    confirmPassword: '',
    changePasswordSuccess: false,
    changePasswordSuccessMsg: '',
    changePasswordFailure: false,
    changePasswordFailureMsg: ''
  },
  methods: {
    changePassword: function(event) {
      event.preventDefault();
      var $form = $('#changePasswordForm');

      this.$http.headers.common['X-CSRF-TOKEN'] = document.querySelector('[name="csrfToken"]').getAttribute('value');
      this.$http.post($form.attr('action'), $form.serialize(), {headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
        .then(function(success) {
          this.changePasswordSuccess = true;
          this.changePasswordSuccessMsg = success.data;
          this.changePasswordFailure = false;
        }, function(failure) {
          this.changePasswordFailure = true;
          this.changePasswordFailureMsg = failure.data;
          this.changePasswordSuccess = false;
        });
    }
  },
  validations: {
    currentPassword: {
      required
    },
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
