import { createDictAccessor } from './accessor'

export const dict = createDictAccessor({
  status: { code: 'status', valueType: 'NUMBER' },
  dataValueType: { code: 'data_type', valueType: 'STRING' },
  builtIn: { code: 'built_in', valueType: 'NUMBER' },
  deleted: { code: 'deleted', valueType: 'NUMBER' },
  postLevel: { code: 'post_level', valueType: 'NUMBER' },
  gender: { code: 'gender', valueType: 'STRING' },
  menuType: { code: 'menu_type', valueType: 'STRING' },
  requestMethod: { code: 'request_method', valueType: 'STRING' },
  subjectType: { code: 'subject_type', valueType: 'STRING' },
  relationType: { code: 'relation_type', valueType: 'STRING' },
})

export type { DictItem, DictAccessor, DataValueType } from './types'
