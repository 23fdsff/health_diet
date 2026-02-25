const app = getApp();
const { request } = require('../../utils/request.js');

Page({
  data: {
    activeTab: 'user',
    username: '',
    password: ''
  },

  switchTab(e) {
    this.setData({ activeTab: e.currentTarget.dataset.tab });
  },

  onInputUsername(e) {
    this.setData({ username: e.detail.value });
  },

  onInputPassword(e) {
    this.setData({ password: e.detail.value });
  },

  // 普通用户微信登录
  onWxUserInfo() {
    wx.login({
      success: (res) => {
        if (res.code) {
          request({
            url: '/api/auth/wxLogin',
            method: 'POST',
            data: { jsCode: res.code },
            success: (data) => {
              app.globalData.token = data.token;
              app.globalData.userInfo = data;
              wx.setStorageSync('token', data.token);
              wx.setStorageSync('userInfo', data);
              wx.reLaunch({ url: '/pages/user/home/home' });
            }
          });
        }
      }
    });
  },

  // 管理端账号密码登录
  onAccountLogin() {
    if (!this.data.username || !this.data.password) {
      wx.showToast({ title: '请输入账号和密码', icon: 'none' });
      return;
    }
    request({
      url: '/api/auth/login',
      method: 'POST',
      header: { 'content-type': 'application/x-www-form-urlencoded' },
      data: {
        username: this.data.username,
        password: this.data.password
      },
      success: (data) => {
        app.globalData.token = data.token;
        app.globalData.userInfo = data;
        wx.setStorageSync('token', data.token);
        wx.setStorageSync('userInfo', data);

        const role = data.roleCodes && data.roleCodes[0];
        // 目前只生成了普通用户首页，管理端可后续扩展
        if (role === 'ROLE_ADMIN') {
          wx.showToast({ title: '管理员登录成功', icon: 'success' });
        } else if (role === 'ROLE_DIETITIAN') {
          wx.showToast({ title: '营养师登录成功', icon: 'success' });
        } else if (role === 'ROLE_ENTERPRISE') {
          wx.showToast({ title: '企业用户登录成功', icon: 'success' });
        }
        wx.reLaunch({ url: '/pages/user/home/home' });
      }
    });
  }
});

