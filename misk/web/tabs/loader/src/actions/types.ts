export interface IActionType {
  ITEM: ITEM
  LOADER: LOADER
}

export enum ITEM {
  GET = "ITEM_GET",
  GET_ONE = "ITEM_GET_ONE",
  SAVE = "ITEM_SAVE",
  PUT = "ITEM_PUT",
  PATCH = "ITEM_PATCH",
  DELETE = "ITEM_DELETE",
  SUCCESS = "ITEM_SUCCESS",
  FAILURE = "ITEM_FAILURE"
}

export enum LOADER {
  FAILURE = "LOADER_FAILURE",
  GET_ALL_TABS = "LOADER_GET_ALL_TABS",
  GET_ONE_COMPONENT = "LOADER_GET_ONE_COMPONENT",
  SUCCESS = "LOADER_SUCCESS"
}