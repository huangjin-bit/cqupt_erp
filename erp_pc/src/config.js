// 配置服务器地址
// export const SERVER = "http://172.22.4.43:9000"
// export const WS = "ws://172.22.4.43:9000"
export const SERVER = window.location.protocol + '//' + window.location.host + '/'
export const WS = (window.location.protocol === 'https:' ? 'wss://' : 'ws://') + window.location.host