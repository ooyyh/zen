<script setup>
import { ref } from 'vue'

const props = defineProps({
  show: Boolean,
  title: String,
  message: String,
  confirmText: { type: String, default: '确定' },
  cancelText: { type: String, default: '取消' },
  type: { type: String, default: 'confirm' } // 'confirm', 'alert', 'success', 'error'
})

const emit = defineEmits(['confirm', 'cancel', 'close'])

const handleConfirm = () => {
  emit('confirm')
  emit('close')
}

const handleCancel = () => {
  emit('cancel')
  emit('close')
}
</script>

<template>
  <Transition name="dialog">
    <div v-if="show" class="dialog-overlay" @click.self="handleCancel">
      <div class="dialog-box">
        <div class="dialog-header">
          <h3>{{ title }}</h3>
        </div>
        <div class="dialog-body">
          <p>{{ message }}</p>
        </div>
        <div class="dialog-footer">
          <button v-if="type === 'confirm'" class="btn ghost" @click="handleCancel">
            {{ cancelText }}
          </button>
          <button 
            class="btn" 
            :class="type === 'error' ? 'danger' : 'primary'" 
            @click="handleConfirm"
          >
            {{ confirmText }}
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog-box {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  animation: dialog-in 0.2s ease-out;
}

@keyframes dialog-in {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.dialog-header {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #e5e7eb;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.dialog-body {
  padding: 20px;
}

.dialog-body p {
  margin: 0;
  color: #4b5563;
  line-height: 1.6;
  font-size: 15px;
}

.dialog-footer {
  padding: 12px 20px 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn {
  padding: 8px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.btn.primary {
  background: #3b82f6;
  color: white;
}

.btn.primary:hover {
  background: #2563eb;
}

.btn.danger {
  background: #dc2626;
  color: white;
}

.btn.danger:hover {
  background: #b91c1c;
}

.btn.ghost {
  background: transparent;
  color: #6b7280;
  border: 1px solid #d1d5db;
}

.btn.ghost:hover {
  background: #f3f4f6;
  border-color: #9ca3af;
}

.dialog-enter-active,
.dialog-leave-active {
  transition: opacity 0.2s;
}

.dialog-enter-from,
.dialog-leave-to {
  opacity: 0;
}

@media (prefers-color-scheme: dark) {
  .dialog-box {
    background: #1f2937;
  }

  .dialog-header {
    border-color: #374151;
  }

  .dialog-header h3 {
    color: #f9fafb;
  }

  .dialog-body p {
    color: #d1d5db;
  }

  .btn.ghost {
    color: #9ca3af;
    border-color: #4b5563;
  }

  .btn.ghost:hover {
    background: #374151;
    border-color: #6b7280;
  }
}
</style>
