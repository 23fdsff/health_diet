const { request } = require('../../../utils/request.js');

Page({
  data: {
    userId: null,
    form: {
      id: null,
      gender: 1,
      age: '',
      heightCm: '',
      weightKg: '',
      chronicDisease: '',
      allergy: '',
      tastePrefer: '',
      goal: ''
    },
    genderOptions: ['男', '女'],
    genderIndex: 0
  },

  onLoad() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    const userId = userInfo.userId || 1;
    this.setData({ userId });
    this.loadProfile();
  },

  loadProfile() {
    request({
      url: '/api/user/profile',
      data: { userId: this.data.userId },
      success: (data) => {
        if (data) {
          const genderIndex = data.gender === 2 ? 1 : 0;
          this.setData({
            form: data,
            genderIndex
          });
        }
      }
    });
  },

  onGenderChange(e) {
    const idx = Number(e.detail.value);
    this.setData({
      genderIndex: idx,
      'form.gender': idx === 1 ? 2 : 1
    });
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`form.${field}`]: value
    });
  },

  onSave() {
    const payload = Object.assign({}, this.data.form, {
      userId: this.data.userId
    });
    request({
      url: '/api/user/profile',
      method: 'POST',
      data: payload,
      success: () => {
        wx.showToast({ title: '保存成功', icon: 'success' });
      }
    });
  }
});

