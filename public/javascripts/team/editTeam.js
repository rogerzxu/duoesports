UPLOADCARE_LOCALE = "en";
UPLOADCARE_TABS = "file url facebook gdrive gphotos dropbox instagram evernote flickr skydrive";
UPLOADCARE_PUBLIC_KEY = '658138b714377dc6889f';
UPLOADCARE_IMAGES_ONLY = true;
UPLOADCARE_PREVIEW_STEP = true;
UPLOADCARE_CLEARABLE = true;

new Vue({
  el: '#editTeamForm',
  data: {
    editTeamSuccess: false,
    editTeamSuccessMsg: '',
    editTeamFailure: false,
    editTeamFailureMsg: ''
  },
  methods: {
    editTeamSubmit: function (event) {
      event.preventDefault();
      var $form = $('#editTeamForm');

      this.$http.headers.common['X-CSRF-TOKEN'] = document.querySelector('[name="csrfToken"]').getAttribute('value');
      this.$http.post($form.attr('action'), $form.serialize(), {headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
        .then(function(success) {
          console.log(success.data);
          this.editTeamSuccess = true;
          this.editTeamSuccessMsg = success.data['message'];
          this.savePlayerFailure = false;
          window.scrollTo(0,0);
        }, function(failure) {
          this.editTeamFailure = true;
          this.editTeamFailureMsg = failure.data['message'];
          this.editTeamSuccess = false;
          window.scrollTo(0,0);
        });
    }
  }
});