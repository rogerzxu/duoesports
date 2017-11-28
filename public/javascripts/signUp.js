Vue.use(window.vuelidate.default);
const { required, email, sameAs, minLength, or } = window.validators;

new Vue({
  el: "#signUpForm",
  data: {
    email: '',
    firstName: '',
    lastName: '',
    summonerName: '',
    password: '',
    confirmPassword: '',
    regionSelected: false
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
    summonerName: {
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
