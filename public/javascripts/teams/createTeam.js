Vue.use(window.vuelidate.default);
Vue.use(VueResource);
var { required } = window.validators;

new Vue({
  el: '#createTeamForm',
  data: {
    teamName: '',
    createTeamFailure: false,
    createTeamFailureMsg: ''
  },
  methods: {
    createTeam: function (event) {
      event.preventDefault();
      var $form = $('#createTeamForm');

      this.$http.headers.common['X-CSRF-TOKEN'] = document.querySelector('[name="csrfToken"]').getAttribute('value');
      this.$http.post($form.attr('action'), $form.serialize(), {headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
        .then(function(success) {
          window.location.href = success.data['message'];
        }, function(failure) {
          this.createTeamFailure = true;
          this.createTeamFailureMsg = failure.data['message'];
          window.scrollTo(0,0);
        });
    }
  },
  validations: {
    teamName: {
      required
    }
  }
});