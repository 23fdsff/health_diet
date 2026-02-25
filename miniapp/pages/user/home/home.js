const { request } = require('../../../utils/request.js');

Page({
  data: {
    nickName: '用户',
    plans: []
  },

  onLoad() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    this.setData({
      nickName: userInfo.nickName || '用户'
    });
    this.loadPlans();
  },

  loadPlans() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    const userId = userInfo.userId || 1;
    const dateStr = new Date().toISOString().slice(0, 10);
    request({
      url: '/api/user/recommendations',
      data: { userId, date: dateStr },
      success: (data) => {
        // 后端目前只返回 ID 和分数，这里先简单填充名称占位。
        const plans = (data || []).map(item => ({
          ...item,
          breakfastName: '推荐早餐 ' + item.planIndex,
          lunchName: '推荐午餐 ' + item.planIndex,
          dinnerName: '推荐晚餐 ' + item.planIndex
        }));
        this.setData({ plans });
      }
    });
  },

  regenerate() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    const userId = userInfo.userId || 1;
    const dateStr = new Date().toISOString().slice(0, 10);
    request({
      url: '/api/user/recommendations/generate',
      method: 'POST',
      data: { userId, date: dateStr },
      success: (data) => {
        wx.showToast({ title: '已生成', icon: 'success' });
        const plans = (data || []).map(item => ({
          ...item,
          breakfastName: '推荐早餐 ' + item.planIndex,
          lunchName: '推荐午餐 ' + item.planIndex,
          dinnerName: '推荐晚餐 ' + item.planIndex
        }));
        this.setData({ plans });
      }
    });
  },

  toProfile() {
    wx.navigateTo({ url: '/pages/user/profile/profile' });
  },

  toDietRecord() {
    wx.navigateTo({ url: '/pages/user/diet-record/diet-record' });
  }
});

