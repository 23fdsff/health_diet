const { request } = require('../../../utils/request.js');

Page({
  data: {
    userId: null,
    date: '',
    mealOptions: ['早餐', '午餐', '晚餐', '加餐'],
    mealIndex: 0,
    form: {
      recipeId: '',
      description: '',
      calories: '',
      satisfaction: ''
    },
    list: [],
    stat: {
      total: 0,
      last7Days: 0
    }
  },

  onLoad() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    const userId = userInfo.userId || 1;
    const today = new Date().toISOString().slice(0, 10);
    this.setData({ userId, date: today });
    this.loadList();
    this.loadStat();
  },

  onDateChange(e) {
    this.setData({ date: e.detail.value });
  },

  onMealChange(e) {
    this.setData({ mealIndex: Number(e.detail.value) });
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`form.${field}`]: value
    });
  },

  onSubmit() {
    const payload = {
      userId: this.data.userId,
      recordDate: this.data.date,
      mealType: this.data.mealIndex + 1,
      recipeId: this.data.form.recipeId ? Number(this.data.form.recipeId) : null,
      description: this.data.form.description,
      calories: this.data.form.calories ? Number(this.data.form.calories) : null,
      satisfaction: this.data.form.satisfaction ? Number(this.data.form.satisfaction) : null
    };

    request({
      url: '/api/user/dietRecord',
      method: 'POST',
      data: payload,
      success: () => {
        wx.showToast({ title: '记录成功', icon: 'success' });
        this.setData({
          form: { recipeId: '', description: '', calories: '', satisfaction: '' }
        });
        this.loadList();
        this.loadStat();
      }
    });
  },

  loadList() {
    request({
      url: '/api/user/dietRecord/list',
      data: { userId: this.data.userId },
      success: (data) => {
        this.setData({ list: data || [] });
      }
    });
  },

  loadStat() {
    request({
      url: '/api/user/dietRecord/stat',
      data: { userId: this.data.userId },
      success: (data) => {
        this.setData({ stat: data || { total: 0, last7Days: 0 } });
      }
    });
  },

  formatMeal(type) {
    const map = {
      1: '早餐',
      2: '午餐',
      3: '晚餐',
      4: '加餐'
    };
    return map[type] || '';
  }
});

