Vue.use(window.vuelidate.default);
var { required, email, sameAs, minLength } = window.validators;

new Vue({
  el: "#signUpForm",
  data: {
    email: '',
    firstName: '',
    lastName: '',
    password: '',
    confirmPassword: '',
    signUpSuccess: true,
    signUpErrorMsg: ''
  },
  methods: {
    signUp: function (event) {
      event.preventDefault();
      var $form = $('#signUpForm');

      this.$http.headers.common['X-CSRF-TOKEN'] = document.querySelector('[name="csrfToken"]').getAttribute('value');
      this.$http.post($form.attr('action'), $form.serialize(), {headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
        .then(function(success) {
          window.location.href = "/signUp/success";
        }, function(failure) {
          this.signUpSuccess = false;
          this.signUpErrorMsg = failure.data['message'];
        });
    }
  },
  validations: {
    firstName: {
      required
    },
    lastName: {
      required
    },
    email: {
      required,
      email
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
