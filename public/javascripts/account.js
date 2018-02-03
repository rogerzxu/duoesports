Vue.use(window.vuelidate.default);
var { required } = window.validators;

new Vue({
  el: "#accountSettingsForm",
  data: {
    firstName: $('#firstName').attr('placeholder'),
    lastName: $('#lastName').attr('placeholder')
  },
  methods: {
    saveAccountSettings: function (event) {
      event.preventDefault();
    }
  },
  validations: {
    firstName: {
      required
    },
    lastName: {
      required
    }
  }
});
