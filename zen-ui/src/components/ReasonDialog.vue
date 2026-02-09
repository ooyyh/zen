<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: Boolean,
  title: {
    type: String,
    default: '请输入原因'
  },
  placeholder: {
    type: String,
    default: '请输入...'
  },
  maxLength: {
    type: Number,
    default: 200
  }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])

const reason = ref('')

watch(() => props.modelValue, (val) => {
  if (val) {
    reason.value = ''
  }
})

const handleConfirm = () => {
  if (!reason.value.trim()) {
    alert('请输入内容')
    return
  }
  emit('confirm', reason.value.trim())
  emit('update:modelValue', false)
}

const handleCancel = () => {
  emit('cancel')
  emit('update:modelValue', false)
}

const handleOverlayClick = (e) => {
  if (e.target === e.currentTarget) {
    handleCancel()
  }
}
</script>

<template>
  <Teleport to="body">
    <Transition name="dialog-fade">
      <div v-if="modelValue" class="dialog-overlay" @click="handleOverlayClick">
        <div class="dialog-box">
          <div class="dialog-header">
            <h3>{{ title }}</h3>
            <button class="close-btn" @click="handleCancel" aria-label="关闭">×</button>
          </div>
          <div class="dialog-body">
            <textarea
              v-model="reason"
              :placeholder="placeholder"
              :maxlength="maxLength"
              rows="4"
              autofocus
            ></textarea>
            <div class="char-count">{{ reason.length }}/{{ maxLength }}</div>
          </div>
          <div class="dialog-footer">
            <button class="btn ghost" @click="handleCancel">取消</button>
            <button class="btn primary" @click="handleConfirm">确定</button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.dialog-box {
  background: white;
  border-radius: 8px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  max-height: 90vh;
}

@media (prefers-color-scheme: dark) {
  .dialog-box {
    background: #1e1e1e;
  }
}

.dialog-header {
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

@media (prefers-color-scheme: dark) {
  .dialog-header {
    border-bottom-color: #333;
  }
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

@media (prefers-color-scheme: dark) {
  .dialog-header h3 {
    color: #f3f4f6;
  }
}

.close-btn {
  background: none;
  border: none;
  font-size: 28px;
  line-height: 1;
  color: #6b7280;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: background 0.2s, color 0.2s;
}

.close-btn:hover {
  background: #f3f4f6;
  color: #111827;
}

@media (prefers-color-scheme: dark) {
  .close-btn:hover {
    background: #333;
    color: #f3f4f6;
  }
}

.dialog-body {
  padding: 24px;
  flex: 1;
  overflow-y: auto;
}

.dialog-body textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  min-height: 100px;
  color: #111827;
  background: white;
}

@media (prefers-color-scheme: dark) {
  .dialog-body textarea {
    background: #2a2a2a;
    border-color: #444;
    color: #f3f4f6;
  }
}

.dialog-body textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: #6b7280;
  margin-top: 8px;
}

.dialog-footer {
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

@media (prefers-color-scheme: dark) {
  .dialog-footer {
    border-top-color: #333;
  }
}

.dialog-fade-enter-active,
.dialog-fade-leave-active {
  transition: opacity 0.2s;
}

.dialog-fade-enter-active .dialog-box,
.dialog-fade-leave-active .dialog-box {
  transition: transform 0.2s, opacity 0.2s;
}

.dialog-fade-enter-from,
.dialog-fade-leave-to {
  opacity: 0;
}

.dialog-fade-enter-from .dialog-box,
.dialog-fade-leave-to .dialog-box {
  transform: scale(0.9);
  opacity: 0;
}
</style>
