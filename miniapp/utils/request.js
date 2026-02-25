const BASE_URL = "http://localhost:8080";

/**
 * 封装 wx.request，统一处理 token 和错误提示。
 */
function request(options) {
  const token = wx.getStorageSync('token');
  const header = options.header || {};
  if (token) {
    header['Authorization'] = 'Bearer ' + token;
  }

  wx.request({
    url: BASE_URL + options.url,
    method: options.method || 'GET',
    data: options.data || {},
    header,
    success(res) {
      if (res.data && res.data.code === 0) {
        options.success && options.success(res.data.data);
      } else {
        wx.showToast({
          title: (res.data && res.data.message) || '请求失败',
          icon: 'none'
        });
        options.fail && options.fail(res);
      }
    },
    fail(err) {
      wx.showToast({
        title: '网络错误',
        icon: 'none'
      });
      options.fail && options.fail(err);
    }
  });
}

module.exports = {
  request,
  BASE_URL
};

