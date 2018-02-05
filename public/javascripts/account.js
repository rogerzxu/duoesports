Vue.use(window.vuelidate.default);
var { required } = window.validators;

new Vue({
  el: "#accountSettingsForm",
  data: {
    firstName: $('#firstName').attr('placeholder'),
    lastName: $('#lastName').attr('placeholder'),
    saveAccountSuccess: false,
    saveAccountSuccessMsg: '',
    saveAccountFailure: false,
    saveAccountFailureMsg: ''
  },
  methods: {
    saveAccountSettings: function (event) {
      event.preventDefault();
      var $form = $('#accountSettingsForm');

      this.$http.headers.common['X-CSRF-TOKEN'] = document.querySelector('[name="csrfToken"]').getAttribute('value');
      this.$http.post($form.attr('action'), $form.serialize(), {headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
        .then(function(success) {
          this.saveAccountSuccess = true;
          this.saveAccountSuccessMsg = success.data['message'];
          this.saveAccountFailure = false;
        }, function(failure) {
          this.saveAccountFailure = true;
          this.saveAccountFailureMsg = failure.data['message'];
          this.saveAccountSuccess = false;
        });
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
