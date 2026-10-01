function useWebsocket(params = 'NoGroup') {
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  const websocket = new WebSocket(
    `${protocol}//${window.location.host}/erpn/commonWebsocket?groupName=${encodeURIComponent(params)}`
  )

  websocket.onmessage(m => {
    console.log(m)
  })

  return null
}

export default useWebsocket
