
import { createItem } from "../controllers/item.controller.js";
import { verifyJWT } from "../middlewares/auth.middleware.js";
import upload from "../middlewares/multer.middleware.js";
import { Router } from "express";

const router = Router();

router.route("/addItem").post(
    verifyJWT,
  upload.fields([
    {
      name: "images",
      maxCount: 1, 
    },
  ]),
  createItem
);


export default router;
