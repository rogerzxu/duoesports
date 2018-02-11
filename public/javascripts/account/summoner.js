new Vue({
  el: "#saveSummonerInfoForm",
  data: {
    saveSummonerSuccess: false,
    saveSummonerSuccessMsg: '',
    saveSummonerFailure: false,
    saveSummonerFailureMsg: '',
    newPrimary: ''
  },
  methods: {
    saveSummonerInfo: function (event) {
      event.preventDefault();
      var $form = $('#saveSummonerInfoForm');

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

    selectAlt: function (event) {
      event.preventDefault();
      $('a').removeClass('active');
      var $newPrimary = $(event.target);
      $newPrimary.addClass('active');
      this.newPrimary = $newPrimary.text();
    }
  }
});