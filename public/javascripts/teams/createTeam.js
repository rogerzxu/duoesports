UPLOADCARE_LOCALE = "en";
UPLOADCARE_TABS = "file url facebook gdrive gphotos dropbox instagram evernote flickr skydrive";
UPLOADCARE_PUBLIC_KEY = '658138b714377dc6889f';
UPLOADCARE_IMAGES_ONLY = true;
UPLOADCARE_PREVIEW_STEP = true;
UPLOADCARE_CLEARABLE = true;

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
          console.log(failure.data['message']);
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