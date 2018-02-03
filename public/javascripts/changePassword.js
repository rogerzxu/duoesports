Vue.use(window.vuelidate.default);
var { required, sameAs, minLength } = window.validators;

new Vue({
  el: "#changePasswordForm",
  data: {
    currentPassword: '',
    password: '',
    confirmPassword: ''
  },
  methods: {
    changePassword: function(event) {
      event.preventDefault();
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
