import api from "./index";

const BOARD_API_BASE_URL = "/boards";

const getBoardList = async () => {
  const { data } = await api.get(BOARD_API_BASE_URL);
  console.log(data);
  return data;
};

const getBoardDetail = async (boardNo) => {
  const { data } = await api.get(`${BOARD_API_BASE_URL}/${boardNo}`);
  return data;
};

const removeBoard = async (boardNo) => {
  await api.delete(`${BOARD_API_BASE_URL}/${boardNo}`);
};

const addBoard = async (form) => {
  const { data } = await api.post(BOARD_API_BASE_URL, form);
  return data;
};
export { getBoardList, getBoardDetail, removeBoard, addBoard };
