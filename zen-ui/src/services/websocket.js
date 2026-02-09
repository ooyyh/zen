import { Client } from '@stomp/stompjs'

class WebSocketService {
  constructor() {
    this.client = null
    this.connected = false
    this.subscribers = new Map()
  }

  connect(onConnected) {
    this.client = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      debug: (str) => console.log('[STOMP]', str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    })

    this.client.onConnect = () => {
      console.log('WebSocket connected')
      this.connected = true
      if (onConnected) onConnected()
    }

    this.client.onDisconnect = () => {
      console.log('WebSocket disconnected')
      this.connected = false
    }

    this.client.onStompError = (frame) => {
      console.error('STOMP error', frame)
    }

    this.client.activate()
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate()
      this.connected = false
    }
  }

  subscribe(topic, callback) {
    if (!this.client) return null

    const subscription = this.client.subscribe(topic, (message) => {
      const data = JSON.parse(message.body)
      callback(data)
    })

    this.subscribers.set(topic, subscription)
    return subscription
  }

  unsubscribe(topic) {
    const subscription = this.subscribers.get(topic)
    if (subscription) {
      subscription.unsubscribe()
      this.subscribers.delete(topic)
    }
  }

  send(destination, body) {
    if (this.client && this.connected) {
      this.client.publish({
        destination,
        body: JSON.stringify(body)
      })
    }
  }
}

export default new WebSocketService()
