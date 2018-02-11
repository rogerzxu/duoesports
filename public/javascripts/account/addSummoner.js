Vue.use(window.vuelidate.default);
var { required } = window.validators;

new Vue({
  el: "#addSummonerForm",
  data: {
    verificationCode: '',
    generateVerificationFailure: false,
    generateVerificationFailureMsg: '',
    summonerName: '',
    addSummonerSuccess: false,
    addSummonerSuccessMsg: '',
    addSummonerFailure: false,
    addSummonerFailureMsg: ''
  },
  methods: {
    addSummoner: function (event) {
      event.preventDefault();
      var $form = $('#addSummonerForm');

      this.$http.headers.common['X-CSRF-TOKEN'] = document.querySelector('[name="csrfToken"]').getAttribute('value');
      this.$http.post($form.attr('action'), $form.serialize(), {headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
        .then(function(success) {
          this.saveSummonerSuccess = true;
          this.saveSummonerSuccessMsg = success.data['message'];
          this.saveSummonerFailure = false;
        }, function(failure) {
          this.saveSummonerFailure = true;
          this.saveSummonerFailureMsg = failure.data['message'];
          this.saveSummonerSuccess = false;
        });
    },
    generateCode: function (event) {
      event.preventDefault();
      this.$http.get("/account/verification-code")
        .then(function(success) {
          this.verificationCode = success.data['data'][0]['code'];
        }, function(failure) {
          this.generateVerificationFailure = true;
          this.generateVerificationFailureMsg = failure.data['message'];
        });
    }
  },
  validations: {
    verificationCode: {
      required
    },
    summonerName: {
      required
    }
  }
});
