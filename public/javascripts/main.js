new Vue({
  el: '#navbar',
  data: {
    isHidden: true
  },
  methods: {
    showSignIn: function (event) {
      this.isHidden = !this.isHidden
    },
    showProfileMenu: function (event) {
      this.isHidden = !this.isHidden
    }
  }
});