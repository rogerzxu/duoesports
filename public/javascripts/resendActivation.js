new Vue({
  el: "#resendActivation",
  data: {
    hideResentMsg: true,
    resentActivationMsg: ''
  },
  methods: {
    resendActivation: function (event) {
      event.preventDefault();
      var $form = $('#resendActivationForm');

      this.$http.get($form.attr('action'))
        .then(function(success) {
          this.resentActivationMsg = success.data;
        }, function(failure) {
          this.resentActivationMsg = failure.data;
        });
      this.hideResentMsg = false;
    }
  }
});