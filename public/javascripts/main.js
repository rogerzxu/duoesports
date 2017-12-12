Vue.use(window.vuelidate.default);
Vue.use(VueResource);
var { required } = window.validators;

new Vue({
  el: '#navbar',
  data: {
    isHidden: true,
    signInSuccess: true,
    signInErrorMsg: '',
    signInEmail: '',
    signInPassword: ''
  },
  methods: {
    showSignIn: function (event) {
      this.isHidden = !this.isHidden
    },

    showProfileMenu: function (event) {
      this.isHidden = !this.isHidden
    },

    signIn: function (event) {
      event.preventDefault();
      var $form = $('#signInForm');

      this.$http.headers.common['X-CSRF-TOKEN'] = document.querySelector('[name="csrfToken"]').getAttribute('value');
      this.$http.post($form.attr('action'), $form.serialize(), {headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
        .then(this.successCallBack, this.errorCallBack);
    },

    successCallBack: function (response) {
      window.location.href = "/";
    },

    errorCallBack: function (response) {
      this.signInErrorMsg = response.data;
      this.signInSuccess = false;
    }
  },
  validations: {
    signInEmail: {
      required
    },
    signInPassword: {
      required
    }
  }
});
