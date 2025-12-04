-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 04, 2025 at 02:03 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `astronout_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `astronout_user`
--

CREATE TABLE `astronout_user` (
  `astronout_id` int(11) NOT NULL,
  `astronout_username` varchar(30) NOT NULL,
  `astronout_password` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `astronout_user`
--

INSERT INTO `astronout_user` (`astronout_id`, `astronout_username`, `astronout_password`) VALUES
(1, 'admin', '12345'),
(2, 'player1', 'password'),
(3, 'tes', '123');

-- --------------------------------------------------------

--
-- Table structure for table `game_saves`
--

CREATE TABLE `game_saves` (
  `save_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `day_count` int(11) DEFAULT 1,
  `oxygen_level` int(11) DEFAULT 100,
  `food_level` int(11) DEFAULT 100,
  `power_level` int(11) DEFAULT 100,
  `is_finished` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `game_saves`
--

INSERT INTO `game_saves` (`save_id`, `user_id`, `day_count`, `oxygen_level`, `food_level`, `power_level`, `is_finished`) VALUES
(1, 3, 3, 52, 70, 100, 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `astronout_user`
--
ALTER TABLE `astronout_user`
  ADD PRIMARY KEY (`astronout_id`),
  ADD UNIQUE KEY `username` (`astronout_username`);

--
-- Indexes for table `game_saves`
--
ALTER TABLE `game_saves`
  ADD PRIMARY KEY (`save_id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `astronout_user`
--
ALTER TABLE `astronout_user`
  MODIFY `astronout_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `game_saves`
--
ALTER TABLE `game_saves`
  MODIFY `save_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `game_saves`
--
ALTER TABLE `game_saves`
  ADD CONSTRAINT `game_saves_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `astronout_user` (`astronout_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
