UPLOADCARE_LOCALE = "en";
UPLOADCARE_TABS = "file url facebook gdrive gphotos dropbox instagram evernote flickr skydrive";
UPLOADCARE_PUBLIC_KEY = 'e9387425d484705e6fd6';
UPLOADCARE_IMAGES_ONLY = true;
UPLOADCARE_PREVIEW_STEP = true;
UPLOADCARE_CLEARABLE = true;

new Vue({
  el: "#playerInfoForm",
  data: {
    savePlayerSuccess: false,
    savePlayerSuccessMsg: '',
    savePlayerFailure: false,
    savePlayerFailureMsg: ''
  },
  methods: {
    savePlayerInfo: function (event) {
      event.preventDefault();
      var $form = $('#playerInfoForm');

      this.$http.headers.common['X-CSRF-TOKEN'] = document.querySelector('[name="csrfToken"]').getAttribute('value');
      this.$http.post($form.attr('action'), $form.serialize(), {headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
        .then(function(success) {
          this.savePlayerSuccess = true;
          this.savePlayerSuccessMsg = success.data['message'];
          this.savePlayerFailure = false;
          window.scrollTo(0,0);
        }, function(failure) {
          this.savePlayerFailure = true;
          this.savePlayerFailureMsg = failure.data['message'];
          this.savePlayerSuccess = false;
          window.scrollTo(0,0);
        });
    }
  }
});
