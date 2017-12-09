Vue.use(window.vuelidate.default);
const { required, email, sameAs, minLength, or } = window.validators;

new Vue({
  el: "#signUpForm",
  data: {
    email: '',
    firstName: '',
    lastName: '',
    password: '',
    confirmPassword: ''
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
