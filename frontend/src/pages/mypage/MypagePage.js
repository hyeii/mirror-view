import { Route, Routes, useNavigate, Navigate } from "react-router-dom";
import { useCallback, useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";

import ChangePassword from "./ChangePasswordPage";
import Feedback from "./feedback/FeedbackPage";
import MyEssay from "./essay/EssayListPage";
import EssayCreate from "./essay/EssayCreatePage";
import EssayDetail from "./essay/EssayDetailPage";
import EssayUpdate from "./essay/EssayUpdatePage";
import Profile from "./profile/ProfilePage";
import ChangeEmail from "./profile/ChangeEmailPage";
import ChangeNickname from "./profile/ChangeNicknamePage";
import { getUserInfo } from "../../store/AuthStore";

import Header from "../../components/common/HeaderComponent";
import Footer from "../../components/common/FooterComponent";

const MyPage = () => {
    const { user } = useSelector((state) => state.auth);

    // ---- 임의 사용자 생성 ----
    // const user = {
    //     userId: "123",
    //     nickname: "뀨",
    //     email: "ssafy@ssafy",
    //     averageRating: 3.4,
    //     password: "123",
    // };
    // ------------------------

    const navigate = useNavigate();
    const dispatch = useDispatch();

    // useEffect(() => {
    //     if(!user){
    //         navigate("/login");
    //     }
    //     return () =>{
    //     }

    // }, []);

    return (
        <div>
            <Header />

            <Routes>
                {user ? (
                    <>
                        <Route
                            path="changepassword"
                            element={<ChangePassword />}
                        />
                        <Route path="feedback" element={<Feedback />} />
                        <Route path="profile" element={<Profile />} />
                        <Route path="changeemail" element={<ChangeEmail />} />
                        <Route
                            path="changenickname"
                            element={<ChangeNickname />}
                        />
                        <Route path="myessay" element={<MyEssay />} />
                        <Route path="essaycreate" element={<EssayCreate />} />
                        <Route
                            path="essaydetail/:id"
                            element={<EssayDetail />}
                        />
                        <Route
                            path="essaydetail/:id/update"
                            element={<EssayUpdate />}
                        />
                        <Route
                            path="changepassword"
                            element={<ChangePassword />}
                        />
                    </>
                ) : (
                    <Route
                        path="*"
                        element={<Navigate replace to="/login" />}
                    />
                )}
            </Routes>
            <Footer />
        </div>
    );
};

export default MyPage;
